package com.example.quest1pos.data.model.orders

data class Order(
    val id: String,
    val terminalId: String,
    val storeId: String,
    val status: String, // Consider making this an enum class (e.g., "PENDING", "COMPLETED")
    val createdAt: Long, // Unix timestamp
    val totalAmount: Double,
    val currency: String,
    val items: List<OrderItem>
)
