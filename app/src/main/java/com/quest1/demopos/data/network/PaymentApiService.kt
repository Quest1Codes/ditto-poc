package com.quest1.demopos.data.network

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Request body for initiating a payment.
 */
@JsonClass(generateAdapter = true)
data class PaymentRequest(
    val orderId: String,
    val amount: Double,
    val currency: String,
    val paymentMethod: String = "card" // Default to card
)

/**
 * Response body received after a payment is processed.
 */
@JsonClass(generateAdapter = true)
data class PaymentResponse(
    val transactionId: String,
    val status: String, // e.g., "SUCCESS", "FAILED"
    val acquirerId: String,
    val acquirerName: String,
    val orderId: String,
    val totalAmount: Double,
    val failureReason: String? = null
)

/**
 * Retrofit interface for the payment gateway service.
 * This defines the endpoints for processing payments.
 */
interface PaymentApiService {
    /**
     * Submits a payment request to a specific acquirer.
     *
     * @param acquirerId The ID of the acquirer processing the payment.
     * @param request The payment request details.
     * @return A response containing the transaction details.
     */
    @POST("{acquirerId}/pay")
    suspend fun processPayment(
        @Path("acquirerId") acquirerId: String,
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
}