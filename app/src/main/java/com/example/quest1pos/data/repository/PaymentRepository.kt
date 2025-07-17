package com.example.quest1pos.data.repository

import com.example.quest1pos.data.model.payment.Gateway
import com.example.quest1pos.data.model.payment.GatewayPerformanceMetrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor() {

    // --- Stub Data ---
    private val stubGateways = listOf(
        Gateway(
            id = "gw_stripe",
            name = "Stripe",
            apiEndpoints = mapOf("charge" to "https://api.stripe.com/v1/charges"),
            supportedPaymentMethods = listOf("VISA", "MASTERCARD", "AMEX")
        ),
        Gateway(
            id = "gw_paypal",
            name = "PayPal",
            apiEndpoints = mapOf("payment" to "https://api.paypal.com/v1/payments/payment"),
            supportedPaymentMethods = listOf("PAYPAL_CREDIT", "VISA")
        )
    )

    private val stubMetrics = listOf(
        GatewayPerformanceMetrics(
            id = "met_01",
            transactionId = "txn_01",
            gatewayId = "gw_stripe",
            terminalId = "term_A1",
            timestamp = System.currentTimeMillis(),
            metrics = mapOf("latencyMs" to 120),
            wasSuccess = true,
            failureCode = null
        )
    )

    // --- Repository Functions ---

    fun getAvailableGateways(): Flow<List<Gateway>> = flow {
        emit(stubGateways)
    }

    fun getPerformanceMetrics(gatewayId: String): Flow<List<GatewayPerformanceMetrics>> = flow {
        emit(stubMetrics.filter { it.gatewayId == gatewayId })
    }
}