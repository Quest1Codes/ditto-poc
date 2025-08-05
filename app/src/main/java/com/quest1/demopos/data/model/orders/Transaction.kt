package com.quest1.demopos.data.model.orders

// Represents a financial transaction record to be stored in Ditto.
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