package com.quest1.demopos.domain.usecase.order

import android.util.Log
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import com.quest1.demopos.data.repository.DittoRepository
import com.quest1.demopos.data.repository.OrderRepository
import com.quest1.demopos.data.repository.PaymentRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Use case to process a payment for the active order.
 * It selects a random acquirer, simulates the transaction, logs the result,
 * and returns the payment response.
 */
class ProcessPaymentUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val dittoRepository: DittoRepository
) {
    suspend fun execute(): Result<PaymentResponse> {
        return try {
            // 1. Get the current active order.
            val activeOrder = orderRepository.observeActiveOrder().first()
                ?: return Result.failure(Exception("No active order found."))

            // 2. Get the list of available payment gateways.
            val gateways = paymentRepository.getAvailableGateways().first()
            if (gateways.isEmpty()) {
                return Result.failure(Exception("No payment gateways available."))
            }

            // 3. Choose a random acquirer.
            val selectedAcquirer = gateways.random()

            // 4. Start the timestamp and process the payment.
            val startTime = System.currentTimeMillis()
            val paymentRequest = PaymentRequest(
                orderId = activeOrder.id,
                amount = activeOrder.totalAmount,
                currency = activeOrder.currency
            )
            val response = paymentRepository.processPayment(selectedAcquirer, paymentRequest)
            val endTime = System.currentTimeMillis()
            val latency = endTime - startTime

            // Format timestamp for logging
            val readableTimestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date(startTime))

            // 5. Create a transaction log.
            val transaction = Transaction(
                id = response.transactionId,
                orderId = activeOrder.id,
                acquirerId = selectedAcquirer.id,
                acquirerName = selectedAcquirer.name,
                status = response.status,
                amount = response.totalAmount,
                currency = activeOrder.currency,
                failureReason = response.failureReason,
                latencyMs = latency,
                createdAt = startTime
            )

            // 6. Log the results.
            Log.d("ProcessPaymentUseCase", """
                --- Payment Attempt Log ---
                Acquirer Name: ${transaction.acquirerName}
                Acquirer ID: ${transaction.acquirerId}
                Status: ${transaction.status}
                Total Amount: ${transaction.amount}
                Timestamp: $readableTimestamp
                Latency: ${transaction.latencyMs}ms
                Failure Reason: ${transaction.failureReason ?: "N/A"}
            """.trimIndent())

            Result.success(response)
        } catch (e: Exception) {
            Log.e("ProcessPaymentUseCase", "Payment processing failed", e)
            Result.failure(e)
        }
    }
}