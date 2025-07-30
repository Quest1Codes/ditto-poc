package com.quest1.demopos.domain.usecase

import android.util.Log
import com.quest1.demopos.data.model.analytics.GatewayPerformance
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.model.payment.GatewayPerformanceMetrics
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import com.quest1.demopos.data.repository.GatewayPerformanceRepositoryImpl
import com.quest1.demopos.data.repository.OrderRepository
import com.quest1.demopos.data.repository.PaymentRepository
import com.quest1.demopos.data.repository.TransactionRepository
import com.quest1.demopos.domain.usecase.MabGatewaySelector
import com.quest1.demopos.domain.usecase.GatewayPerformanceData
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val transactionRepository: TransactionRepository,
    private val mabGatewaySelector: MabGatewaySelector,
    private val performanceData: GatewayPerformanceData,
    private val gatewayPerformanceRepository: GatewayPerformanceRepositoryImpl,

    ) {
    suspend fun execute(): Result<PaymentResponse> {
        val activeOrder = orderRepository.observeActiveOrder().first()
            ?: return Result.failure(Exception("No active order found."))

        val selectedAcquirer = mabGatewaySelector.selectGateway()
        val startTime = System.currentTimeMillis()
        var paymentResponse: PaymentResponse? = null
        var exception: Exception? = null

        try {
            val paymentRequest = PaymentRequest(
                orderId = activeOrder.id,
                amount = activeOrder.totalAmount,
                currency = activeOrder.currency
            )
            paymentResponse = paymentRepository.processPayment(selectedAcquirer, paymentRequest)
            val endTime = System.currentTimeMillis()
            val latency = endTime - startTime

            val transaction = Transaction(
                id = paymentResponse.transactionId,
                orderId = activeOrder.id,
                acquirerId = selectedAcquirer.id,
                acquirerName = selectedAcquirer.name,
                status = paymentResponse.status,
                amount = paymentResponse.totalAmount,
                currency = activeOrder.currency,
                failureReason = paymentResponse.failureReason,
                latencyMs = latency,
                createdAt = startTime
            )
            transactionRepository.saveTransaction(transaction)

            if (paymentResponse.status == "SUCCESS") {
                val completedOrder = activeOrder.copy(status = Order.STATUS_COMPLETED)
                orderRepository.updateOrder(completedOrder)
            }

            return Result.success(paymentResponse)
        } catch (e: Exception) {
            exception = e
            Log.e("ProcessPaymentUseCase", "Payment processing failed with an exception", e)
            val failedTransaction = Transaction(
                id = "txn_exc_${UUID.randomUUID()}",
                orderId = activeOrder.id,
                acquirerId = selectedAcquirer.id,
                acquirerName = selectedAcquirer.name,
                status = "FAILED",
                amount = activeOrder.totalAmount,
                currency = activeOrder.currency,
                failureReason = "Exception: ${e.message}",
                latencyMs = System.currentTimeMillis() - startTime,
                createdAt = startTime
            )
            transactionRepository.saveTransaction(failedTransaction)
            return Result.failure(e)
        } finally {
            val wasSuccess = paymentResponse?.status == "SUCCESS" && exception == null
            val latency = System.currentTimeMillis() - startTime

            val metric = GatewayPerformanceMetrics(
                transactionId = paymentResponse?.transactionId ?: "txn_exc_${UUID.randomUUID()}",
                gatewayId = selectedAcquirer.id,
                terminalId = activeOrder.terminalId, // <-- ADDED
                timestamp = System.currentTimeMillis(),
                metrics = mapOf("latencyMs" to latency),
                wasSuccess = wasSuccess,
                failureCode = paymentResponse?.failureReason ?: exception?.message
            )
            performanceData.metrics.add(metric)

            // Log the newly added metric for debugging
            Log.d("MAB_METRIC_ADDED", "New Metric: $metric")

            updatePerformanceMetrics(
                gatewayId = selectedAcquirer.id,
                gatewayName = selectedAcquirer.name,
                wasSuccess = paymentResponse?.status == "SUCCESS"
            )

        }
    }

    private suspend fun updatePerformanceMetrics(gatewayId: String, gatewayName: String, wasSuccess: Boolean) {
        val allPerformance = gatewayPerformanceRepository.observePerformanceRankings().first()
        val currentPerf = allPerformance.find { it.gatewayId == gatewayId }

        val totalAttempts = (currentPerf?.totalAttempts ?: 0) + 1
        val totalSuccesses = (currentPerf?.totalSuccesses ?: 0) + if (wasSuccess) 1 else 0
        val successRate = if (totalAttempts > 0) totalSuccesses.toDouble() / totalAttempts.toDouble() else 0.0

        val newPerformance = GatewayPerformance(
            _id = gatewayId,
            gatewayId = gatewayId,
            gatewayName = gatewayName,
            totalAttempts = totalAttempts,
            totalSuccesses = totalSuccesses,
            successRate = successRate
        )

        gatewayPerformanceRepository.upsertPerformance(newPerformance)

        Log.d(
            "MAB_PERFORMANCE_UPDATE",
            "Gateway: $gatewayName (ID: $gatewayId), " +
                    "Attempts: $totalAttempts, " +
                    "Successes: $totalSuccesses, " +
                    "Success Rate: ${"%.2f".format(successRate)}"
        )
    }
}