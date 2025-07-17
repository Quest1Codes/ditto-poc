package com.quest1.demopos.data.model.payment

enum class PaymentMethod {
    VISA,
    MASTERCARD,
    AMEX,
    PAYPAL_CREDIT
}

data class Gateway(
    val id: String,
    val name: String,
    val apiEndpoints: Map<String, String>,
    val supportedPaymentMethods: List<PaymentMethod>
)
