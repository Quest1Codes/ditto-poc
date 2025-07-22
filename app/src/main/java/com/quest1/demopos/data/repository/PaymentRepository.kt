package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PaymentRepository @Inject constructor() {

    // A list of potential failure reasons for a failed transaction.
    private val failureReasons = listOf(
        "Insufficient funds",
        "Card declined by issuer",
        "Expired card",
        "Invalid CVC",
        "Transaction blocked by fraud filter",
        "Gateway timeout"
    )

    // --- Stub Data with updated IDs and Endpoints ---
    private val stubGateways = listOf(
        Gateway(
            id = "stripe21", // New ID format
            name = "Stripe",
            apiEndpoint = "https://payment-process/stripe21/pay", // New URL format
            supportedPaymentMethod = "Credit Card"
        ),
        Gateway(
            id = "adyen34", // New ID format
            name = "Adyen",
            apiEndpoint = "https://payment-process/adyen34/pay", // New URL format
            supportedPaymentMethod = "Credit Card"
        ),
        Gateway(
            id = "paypal56", // New ID format
            name = "PayPal",
            apiEndpoint = "https://payment-process/paypal56/pay", // New URL format
            supportedPaymentMethod = "Credit Card"
        )
    )

    // --- Repository Functions ---

    fun getAvailableGateways(): Flow<List<Gateway>> = flow {
        emit(stubGateways)
    }

    /**
     * Simulates processing a payment with a given acquirer.
     * This function introduces a random delay and randomly determines success or failure.
     */
    suspend fun processPayment(acquirer: Gateway, request: PaymentRequest): PaymentResponse {
        // Simulate network latency with a random delay.
        val waitTime = Random.nextLong(500, 2000)
        delay(waitTime)

        // Success probability is now 80% (fails if random number is 1 or 2)
        val isSuccess = Random.nextInt(1, 11) > 2

        return if (isSuccess) {
            PaymentResponse(
                transactionId = "txn_${UUID.randomUUID()}",
                status = "SUCCESS",
                acquirerId = acquirer.id,
                acquirerName = acquirer.name,
                orderId = request.orderId,
                totalAmount = request.amount
            )
        } else {
            PaymentResponse(
                transactionId = "txn_${UUID.randomUUID()}",
                status = "FAILED",
                acquirerId = acquirer.id,
                acquirerName = acquirer.name,
                orderId = request.orderId,
                totalAmount = request.amount,
                failureReason = failureReasons.random() // Pick a random failure reason.
            )
        }
    }
}
