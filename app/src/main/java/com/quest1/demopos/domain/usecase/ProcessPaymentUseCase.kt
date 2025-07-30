package com.quest1.demopos.domain.usecase.order

import android.util.Log
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.model.payment.Gateway // <-- IMPORT a
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import com.quest1.demopos.data.repository.OrderRepository
import com.quest1.demopos.data.repository.PaymentRepository
import com.quest1.demopos.data.repository.TransactionRepository
import kotlinx.coroutines.flow.first // <-- IMPORT b
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import java.util.UUID

class ProcessPaymentUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun execute(): Result<PaymentResponse> {

        // 1. Get the current active order.
        val activeOrder = orderRepository.observeActiveOrder().first()
            ?: return Result.failure(Exception("No active order found."))

        // 2. Get available payment gateways.
        val gateways = paymentRepository.getAvailableGateways().first()
        if (gateways.isEmpty()) {
            return Result.failure(Exception("No payment gateways available."))
        }

        // 3. Define the acquirer and start time BEFORE the try block to make them available to the catch block.
        val selectedAcquirer = gateways.random()
        val startTime = System.currentTimeMillis()
        return try {
            val paymentRequest = PaymentRequest(
                orderId = activeOrder.id,
                amount = activeOrder.totalAmount,
                currency = activeOrder.currency
            )
            val response = paymentRepository.processPayment(selectedAcquirer, paymentRequest)
            val endTime = System.currentTimeMillis()
            val latency = endTime - startTime

            // 4. Create a transaction record from the response.
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

            // 5. Save the transaction record to Ditto.
            transactionRepository.saveTransaction(transaction)
            Log.d("ProcessPaymentUseCase", "Transaction record saved to Ditto: ${transaction.id}")
            Log.d("ProcessPaymentUseCase", "Transaction status: ${response.status}")



            // 6. If successful, update the order status to "COMPLETED".
            if (response.status == "SUCCESS") {
                val completedOrder = activeOrder.copy(status = Order.STATUS_COMPLETED)
                orderRepository.updateOrder(completedOrder)
            }

            Result.success(response)
        } catch (e: Exception) {
            Log.e("ProcessPaymentUseCase", "Payment processing failed with an exception", e)

            // Create and save a FAILED transaction record for the exception.
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
            Log.w("ProcessPaymentUseCase", "Saved failed transaction record for exception: ${failedTransaction.id}")

            Result.failure(e)
        }
    }
}
