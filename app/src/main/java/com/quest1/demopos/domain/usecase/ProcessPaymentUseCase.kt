package com.quest1.demopos.domain.usecase

import android.util.Log
import com.quest1.demopos.data.model.analytics.GatewayPerformance
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import com.quest1.demopos.data.repository.DittoRepository
import com.quest1.demopos.data.repository.PaymentRepository
import com.quest1.demopos.data.repository.SessionManager
import com.quest1.demopos.domain.usecase.order.GetActiveOrderUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.*
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor(
    private val getActiveOrderUseCase: GetActiveOrderUseCase,
    private val paymentRepository: PaymentRepository,
    private val transactionUseCase: TransactionUseCase,
    private val dittoRepository: DittoRepository,
    private val sessionManager: SessionManager
) {
    suspend fun execute(selectedAcquirer: Gateway): Result<PaymentResponse> {
        val activeOrder = getActiveOrderUseCase.execute().firstOrNull()
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
                val completedOrder = activeOrder.copy(status = Order.STATUS_COMPLETED)
                dittoRepository.saveOrder(completedOrder)

                val transaction = Transaction(
                    id = paymentResponse.transactionId,
                    orderId = completedOrder.id,
                    acquirerId = paymentResponse.acquirerId,
                    acquirerName = paymentResponse.acquirerName,
                    status = paymentResponse.status,
                    amount = paymentResponse.totalAmount,
                    currency = activeOrder.currency,
                    failureReason = paymentResponse.failureReason,
                    latencyMs = System.currentTimeMillis() - startTime,
                    createdAt = Date().time
                )
                dittoRepository.saveTransaction(transaction)

                sessionManager.clearActiveOrderId()

                return Result.success(paymentResponse)
            } else {
                throw Exception(paymentResponse.failureReason ?: "Payment failed for an unknown reason.")
            }
        } catch (e: Exception) {
            exception = e
            Log.e("ProcessPaymentUseCase", "Payment processing failed", e)

            // Create a failed transaction record
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
                createdAt = startTime
            )
            dittoRepository.saveTransaction(failedTransaction)
            return Result.failure(e)
        } finally {
            // Update performance metrics
            updateGatewayPerformance(selectedAcquirer)
        }
    }

    private suspend fun updateGatewayPerformance(selectedAcquirer: Gateway) {
        val allTransactions = transactionUseCase.getTransactions().first()
        val gatewayStats = allTransactions.filter { it.acquirerId == selectedAcquirer.id }

        val newTotalAttempts = gatewayStats.size
        val newTotalSuccesses = gatewayStats.count { it.status == "SUCCESS" }
        val newSuccessRate = if (newTotalAttempts > 0) {
            (newTotalSuccesses.toDouble() / newTotalAttempts.toDouble()) * 100
        } else { 0.0 }

        val updatedPerformance = GatewayPerformance(
            _id = selectedAcquirer.id,
            gatewayId = selectedAcquirer.id,
            gatewayName = selectedAcquirer.name,
            totalAttempts = newTotalAttempts.toLong(),
            totalSuccesses = newTotalSuccesses.toLong(),
            successRate = newSuccessRate
        )

        dittoRepository.upsertPerformance(updatedPerformance)
    }
}