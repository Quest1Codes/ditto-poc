package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.model.payment.GatewayPerformanceMetrics
import com.quest1.demopos.data.model.payment.PaymentCard
import com.quest1.demopos.data.model.payment.PaymentMethod
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
            // Use the PaymentMethod enum instead of strings
            supportedPaymentMethods = listOf(PaymentMethod.VISA, PaymentMethod.MASTERCARD, PaymentMethod.AMEX)
        ),
        Gateway(
            id = "gw_paypal",
            name = "PayPal",
            apiEndpoints = mapOf("payment" to "https://api.paypal.com/v1/payments/payment"),
            // Use the PaymentMethod enum instead of strings
            supportedPaymentMethods = listOf(PaymentMethod.PAYPAL_CREDIT, PaymentMethod.VISA)
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