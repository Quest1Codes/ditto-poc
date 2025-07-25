package com.quest1.demopos.data.model.payment

/**
 * Represents a payment gateway/acquirer.
 *
 * @param id The unique identifier for the acquirer (e.g., "stripe21").
 * @param name The display name of the acquirer (e.g., "Stripe").
 * @param apiEndpoint The full URL for the payment processing endpoint.
 * @param supportedPaymentMethod The single payment method supported, as a String.
 */
data class Gateway(
    val id: String,
    val name: String,
    val apiEndpoint: String,
    val supportedPaymentMethod: String
)