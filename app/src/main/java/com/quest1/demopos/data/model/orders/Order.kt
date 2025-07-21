package com.quest1.demopos.data.model.orders
import android.util.Log
import java.util.Date

data class Order(
    val id: String,
    val terminalId: String,
    val storeId: String,
    val status: String, // Consider making this an enum class (e.g., "PENDING", "COMPLETED")
    val createdAt: Date, // Unix timestamp
    val totalAmount: Double,
    val currency: String,
    val items: List<OrderItem>
) {
    companion object {
        // Define the collection name for consistency
        const val COLLECTION_NAME = "orders"

        // Define status constants
        const val STATUS_PENDING = "PENDING"
        const val STATUS_COMPLETED = "COMPLETED"

        /**
         * Creates an Order object from a document map.
         * Returns null if the document cannot be properly mapped.
         */
        fun fromDocument(docMap: Map<String, Any?>): Order? {
            return try {
                // Map the items list from the document
                val itemsList = (docMap["items"] as? List<Map<String, Any?>> ?: emptyList()).mapNotNull { itemMap ->
                    OrderItem(
                        itemId = itemMap["itemId"] as String,
                        name = itemMap["name"] as String,
                        // Cast to Number first to handle both Integer and Long safely
                        quantity = (itemMap["quantity"] as Number).toInt(),
                        cost = (itemMap["cost"] as Number).toInt()
                    )
                }

                Order(
                    id = docMap["_id"].toString(),
                    terminalId = docMap["terminalId"] as String,
                    storeId = docMap["storeId"] as String,
                    status = docMap["status"] as String,
                    totalAmount = (docMap["totalAmount"] as Number).toDouble(),
                    // Cast to Number first to handle both Integer and Long safely
                    createdAt = Date((docMap["createdAt"] as Number).toLong()),
                    currency = docMap["currency"] as String,
                    items = itemsList
                )
            } catch (e: Exception) {
                Log.e("Order", "Error mapping document: $docMap", e)
                null
            }
        }
    }

    /**
     * Converts this Order object to a document map for database storage.
     */
    fun toDocument(): Map<String, Any?> {
        // Convert the nested list of OrderItems into a list of maps
        val orderItemsAsMaps = items.map {
            mapOf(
                "itemId" to it.itemId,
                "name" to it.name,
                "quantity" to it.quantity,
                "cost" to it.cost
            )
        }

        return mapOf(
            "_id" to id,
            "terminalId" to terminalId,
            "storeId" to storeId,
            "status" to status,
            "totalAmount" to totalAmount,
            "createdAt" to createdAt.time,
            "currency" to currency,
            "items" to orderItemsAsMaps
        )
    }
}