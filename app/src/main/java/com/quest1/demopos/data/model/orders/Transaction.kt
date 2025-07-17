package com.quest1.demopos.data.model.orders

data class Transaction(
    val id: String,
    val transactionId: String,
    val gatewayId: String,
    val status: String, // Consider an enum class (e.g., "SUCCESS", "FAILED")
    val completedAt: Long?, // Nullable for pending transactions
    val failureReason: String?
)
