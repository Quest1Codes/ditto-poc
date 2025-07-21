package com.quest1.demopos.data.model.orders

/**
 * Represents a financial transaction record to be stored in Ditto.
 *
 * @param id The unique identifier for the transaction document.
 * @param orderId The ID of the order this transaction is for.
 * @param acquirerId The ID of the payment gateway that processed the transaction.
 * @param acquirerName The name of the payment gateway.
 * @param status The final status of the transaction (e.g., "SUCCESS", "FAILED").
 * @param amount The total amount of the transaction.
 * @param currency The currency used for the transaction.
 * @param failureReason A message explaining why the transaction failed, if applicable.
 * @param latencyMs The time in milliseconds it took for the transaction to complete.
 * @param createdAt A Unix timestamp indicating when the transaction was created.
 */
data class Transaction(
    val id: String,
    val orderId: String,
    val acquirerId: String,
    val acquirerName: String,
    val status: String,
    val amount: Double,
    val currency: String,
    val failureReason: String?,
    val latencyMs: Long,
    val createdAt: Long
) {
    companion object {
        const val COLLECTION_NAME = "transactions"
    }
}