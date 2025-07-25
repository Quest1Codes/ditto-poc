package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.network.PaymentApiService
import com.quest1.demopos.data.network.PaymentRequest
import com.quest1.demopos.data.network.PaymentResponse
import com.quest1.demopos.data.model.payment.GatewayPerformanceMetrics
import com.quest1.demopos.data.model.payment.PaymentCard
//import com.quest1.demopos.data.model.payment.PaymentMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    // Inject the real API service created by Retrofit from our new PaymentModule
    private val paymentApiService: PaymentApiService
) {

    // This list now only provides the names and IDs of the acquirers.
    private val stubGateways = listOf(
        Gateway(id = "stripe21", name = "Stripe", supportedPaymentMethod = "Credit Card", apiEndpoint = ""),
        Gateway(id = "adyen34", name = "Adyen", supportedPaymentMethod = "Credit Card", apiEndpoint = ""),
        Gateway(id = "paypal56", name = "PayPal", supportedPaymentMethod = "Credit Card", apiEndpoint = "")
    )

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


    private val mockPaymentCards = listOf(
        PaymentCard(
            id = "pay_001",
            transactionId = "TXN_2024_001",
            acquirerName = "Stripe",
            amount = "$125.99",
            currency = "USD",
            date = "Jul 21, 2025",
            time = "2:15 PM",
            location = "Downtown Flagship, Main St",
            status = "SUCCESS",
            gatewayId = "gw_stripe"
        ),
        PaymentCard(
            id = "pay_002",
            transactionId = "TXN_2024_002",
            acquirerName = "PayPal",
            amount = "$89.50",
            currency = "USD",
            date = "Jul 21, 2025",
            time = "1:45 PM",
            location = "Uptown Branch, Oak Ave",
            status = "SUCCESS",
            gatewayId = "gw_paypal"
        ),
        PaymentCard(
            id = "pay_003",
            transactionId = "TXN_2024_003",
            acquirerName = "Square",
            amount = "$45.00",
            currency = "USD",
            date = "Jul 21, 2025",
            time = "12:30 PM",
            location = "Downtown Flagship, Main St",
            status = "FAILED",
            gatewayId = "gw_square"
        ),
        PaymentCard(
            id = "pay_004",
            transactionId = "TXN_2024_004",
            acquirerName = "Razorpay",
            amount = "$67.25",
            currency = "USD",
            date = "Jul 21, 2025",
            time = "11:20 AM",
            location = "Mall Kiosk, Center Court",
            status = "SUCCESS",
            gatewayId = "gw_razorpay"
        ),
        PaymentCard(
            id = "pay_005",
            transactionId = "TXN_2024_005",
            acquirerName = "Adyen",
            amount = "$156.75",
            currency = "USD",
            date = "Jul 20, 2025",
            time = "4:10 PM",
            location = "Downtown Flagship, Main St",
            status = "PENDING",
            gatewayId = "gw_adyen"
        ),
        PaymentCard(
            id = "pay_006",
            transactionId = "TXN_2024_006",
            acquirerName = "Worldpay",
            amount = "$234.99",
            currency = "USD",
            date = "Jul 20, 2025",
            time = "3:25 PM",
            location = "Uptown Branch, Oak Ave",
            status = "SUCCESS",
            gatewayId = "gw_worldpay"
        )
    )

    fun getPaymentCards(): Flow<List<PaymentCard>> = flow {
        emit(mockPaymentCards)
    }

    fun getPaymentCardsByStatus(status: String): Flow<List<PaymentCard>> = flow {
        emit(mockPaymentCards.filter { it.status == status })
    }
}