package com.quest1.demopos.domain.usecase

import android.util.Log
import com.quest1.demopos.data.model.analytics.GatewayPerformance
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.model.payment.GatewayPerformanceMetrics
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import com.quest1.demopos.data.repository.GatewayPerformanceRepositoryImpl
import com.quest1.demopos.data.repository.OrderRepository
import com.quest1.demopos.data.repository.PaymentRepository
import com.quest1.demopos.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val transactionRepository: TransactionRepository,
    private val performanceData: GatewayPerformanceData,
    private val gatewayPerformanceRepository: GatewayPerformanceRepositoryImpl,
) {
    suspend fun execute(selectedAcquirer: Gateway): Result<PaymentResponse> {
        val activeOrder = orderRepository.observeActiveOrder().first()
            ?: return Result.failure(Exception("No active order to process."))

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

            if (paymentResponse.status == "SUCCESS") {
                val permanentOrderId = UUID.randomUUID().toString()
                val completedOrder = activeOrder.copy(
                    id = permanentOrderId,
                    status = Order.STATUS_COMPLETED
                )
                orderRepository.saveOrder(completedOrder)

                val transaction = Transaction(
                    id = paymentResponse.transactionId,
                    orderId = permanentOrderId,
                    acquirerId = paymentResponse.acquirerId,
                    acquirerName = paymentResponse.acquirerName,
                    status = paymentResponse.status,
                    amount = paymentResponse.totalAmount,
                    currency = activeOrder.currency,
                    failureReason = paymentResponse.failureReason,
                    latencyMs = System.currentTimeMillis() - startTime,
                    createdAt = Date().time
                )
                transactionRepository.saveTransaction(transaction)
                orderRepository.deleteOrder(activeOrder.id)
                return Result.success(paymentResponse)
            } else {
                throw Exception(paymentResponse.failureReason ?: "Payment failed for an unknown reason.")
            }
        } catch (e: Exception) {
            exception = e
            Log.e("ProcessPaymentUseCase", "Payment processing failed with an exception", e)

            val failedTransaction = Transaction(
                id = "txn_fail_${UUID.randomUUID().toString().substring(0, 8)}",
                orderId = activeOrder.id,
                acquirerId = selectedAcquirer.id,
                acquirerName = selectedAcquirer.name,
                status = "FAILED",
                amount = activeOrder.totalAmount,
                currency = activeOrder.currency,
                failureReason = "Exception: ${e.message}",
                latencyMs = System.currentTimeMillis() - startTime,
                createdAt = startTime,
            )
            transactionRepository.saveTransaction(failedTransaction)
            return Result.failure(e)
        } finally {
            val wasSuccess = paymentResponse?.status == "SUCCESS" && exception == null
            val latency = System.currentTimeMillis() - startTime

            val metric = GatewayPerformanceMetrics(
                transactionId = paymentResponse?.transactionId ?: "txn_fail_${UUID.randomUUID().toString().substring(0, 8)}",
                gatewayId = selectedAcquirer.id,
                terminalId = activeOrder.terminalId,
                timestamp = System.currentTimeMillis(),
                metrics = mapOf(
                    "latencyMs" to latency,
                    "successRate" to 0.0
                ),
                wasSuccess = wasSuccess,
                failureCode = paymentResponse?.failureReason ?: exception?.message
            )

            performanceData.metrics.add(metric)
            Log.d("MAB_METRIC_ADDED", "New Metric: $metric")

            val allGatewayStats = performanceData.metrics.filter { it.gatewayId == selectedAcquirer.id }
            val newTotalAttempts = allGatewayStats.size
            val newTotalSuccesses = allGatewayStats.count { it.wasSuccess }
            val newSuccessRate = if (newTotalAttempts > 0) {
                (newTotalSuccesses.toDouble() / newTotalAttempts.toDouble()) * 100
            } else {
                0.0
            }

            val updatedPerformance = GatewayPerformance(
                _id = selectedAcquirer.id,
                gatewayId = selectedAcquirer.id,
                gatewayName = selectedAcquirer.name,
                totalAttempts = newTotalAttempts.toLong(),
                totalSuccesses = newTotalSuccesses.toLong(),
                successRate = newSuccessRate
            )

            gatewayPerformanceRepository.upsertPerformance(updatedPerformance)
            Log.d(
                "MAB_PERFORMANCE_UPDATE",
                "Gateway: ${selectedAcquirer.name} (ID: ${selectedAcquirer.id}), " +
                        "Attempts: $newTotalAttempts, " +
                        "Successes: $newTotalSuccesses, " +
                        "SuccessRate: ${"%.2f".format(newSuccessRate)}%"
            )
        }
    }
}