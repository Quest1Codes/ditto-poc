package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.network.PaymentApiService
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import com.quest1.demopos.data.stubGateways // Import the centralized stub data

@Singleton
class PaymentRepository @Inject constructor(
    // Inject the real API service created by Retrofit from our new PaymentModule
    private val paymentApiService: PaymentApiService
) {

    fun getAvailableGateways(): Flow<List<Gateway>> = flow {
        emit(stubGateways)
    }

    /**
     * Processes a payment by making a real network call to the backend server.
     */
    suspend fun processPayment(acquirer: Gateway, request: PaymentRequest): PaymentResponse {
        return try {
            // The simulation logic is gone. Now we make a real API call.
            val response = paymentApiService.processPayment(acquirer.id, request)

            if (response.isSuccessful && response.body() != null) {
                // If the server returns a 2xx status code, return the response body.
                response.body()!!
            } else {
                // If the server returns an error (e.g., 400), create a failure response.
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
            // Handle network exceptions (e.g., server is offline).
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
