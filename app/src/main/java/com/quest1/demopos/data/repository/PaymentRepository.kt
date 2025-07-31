package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.network.PaymentApiService
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.quest1.demopos.data.stubGateways // Import the centralized stub data

@Singleton
class PaymentRepository @Inject constructor(
    private val paymentApiService: PaymentApiService
) {

    fun getAvailableGateways(): Flow<List<Gateway>> = flow {
        emit(stubGateways)
    }

    suspend fun processPayment(acquirer: Gateway, request: PaymentRequest): PaymentResponse {
        return try {
            val response = paymentApiService.processPayment(acquirer.id, request)

            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                PaymentResponse(
                    transactionId = "txn_failed",
                    status = "FAILED",
                    acquirerId = acquirer.id,
                    acquirerName = acquirer.name,
                    orderId = request.orderId,
                    totalAmount = request.amount,
                    failureReason = "Gateway Error: ${response.code()} - ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e("PaymentRepository", "Network exception during payment processing for order: ${request.orderId}", e)
            PaymentResponse(
                transactionId = "txn_network_error",
                status = "FAILED",
                acquirerId = acquirer.id,
                acquirerName = acquirer.name,
                orderId = request.orderId,
                totalAmount = request.amount,
                failureReason = "Network error: ${e.message}"
            )
        }
    }
}