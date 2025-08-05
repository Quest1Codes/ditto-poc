package com.quest1.demopos.data.network

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import com.quest1.demopos.data.defaultPaymentMethod

@JsonClass(generateAdapter = true)
data class PaymentRequest(
    val orderId: String,
    val amount: Double,
    val currency: String,
    val paymentMethod: String =  defaultPaymentMethod
)

@JsonClass(generateAdapter = true)
data class PaymentResponse(
    val transactionId: String,
    val status: String,
    val acquirerId: String,
    val acquirerName: String,
    val orderId: String,
    val totalAmount: Double,
    val failureReason: String? = null
)

interface PaymentApiService {
    @POST("{acquirerId}/pay")
    suspend fun processPayment(
        @Path("acquirerId") acquirerId: String,
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
}