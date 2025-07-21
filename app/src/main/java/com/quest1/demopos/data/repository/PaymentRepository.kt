package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.model.payment.PaymentMethod
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

    // --- Stub Data ---
    // A list of famous international acquirers.
    private val stubGateways = listOf(
        Gateway(
            id = "stripe",
            name = "Stripe",
            apiEndpoints = mapOf("pay" to "https://api.example.com/stripe/pay"),
            supportedPaymentMethods = listOf(PaymentMethod.VISA, PaymentMethod.MASTERCARD, PaymentMethod.AMEX)
        ),
        Gateway(
            id = "adyen",
            name = "Adyen",
            apiEndpoints = mapOf("pay" to "https://api.example.com/adyen/pay"),
            supportedPaymentMethods = listOf(PaymentMethod.VISA, PaymentMethod.MASTERCARD)
        ),
        Gateway(
            id = "paypal",
            name = "PayPal",
            apiEndpoints = mapOf("pay" to "https://api.example.com/paypal/pay"),
            supportedPaymentMethods = listOf(PaymentMethod.PAYPAL_CREDIT, PaymentMethod.VISA)
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
        val waitTime = Random.nextLong(500, 3000)
        delay(waitTime)

        val isSuccess = Random.nextBoolean() // Randomly decide if the payment succeeds.

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