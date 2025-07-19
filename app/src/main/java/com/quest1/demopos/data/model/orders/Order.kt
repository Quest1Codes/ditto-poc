package com.quest1.demopos.data.model.orders

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
{
    companion object {
        // Define the collection name for consistency
        const val COLLECTION_NAME = "orders"
        // Define status constants
        const val STATUS_PENDING = "PENDING"
        const val STATUS_COMPLETED = "COMPLETED"
    }
}
