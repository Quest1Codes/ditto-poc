package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val dittoRepository: DittoRepository
) {

    init {
        // Start a subscription to sync all order data from the Ditto cloud.
        val subscriptionQuery = "SELECT * FROM ${Order.COLLECTION_NAME}"
        dittoRepository.startSubscription(subscriptionQuery)
    }

    /**
     * Observes the user's current active (pending) order.
     * It returns a Flow that emits the single pending order or null if none exists.
     */
    fun observeActiveOrder(): Flow<Order?> {
        val query = "SELECT * FROM ${Order.COLLECTION_NAME} WHERE status = :status LIMIT 1"
        val arguments = mapOf("status" to Order.STATUS_PENDING)

        return dittoRepository.observeCollection(query, arguments).map { documents ->
            documents.firstOrNull()?.let { docMap ->
                try {
                    // Manually map the document map to an Order object
                    val itemsList = (docMap["items"] as? List<Map<String, Any?>> ?: emptyList()).mapNotNull { itemMap ->
                        OrderItem(
                            itemId = itemMap["itemId"] as String,
                            name = itemMap["name"] as String,
                            // FIX: Cast to Number first to handle both Integer and Long safely.
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
                        // FIX: Cast to Number first to handle both Integer and Long safely.
                        createdAt = (docMap["createdAt"] as Number).toLong(),
                        currency = docMap["currency"] as String,
                        items = itemsList
                    )
                } catch (e: Exception) {
                    println("Error mapping order document: $docMap, error: $e")
                    null
                }
            }
        }
    }

    /**
     * Inserts or updates an Order in the database.
     */
    suspend fun upsertOrder(order: Order) {
        // Convert the nested list of OrderItems into a list of maps
        val orderItemsAsMaps = order.items.map {
            mapOf(
                "itemId" to it.itemId,
                "name" to it.name,
                "quantity" to it.quantity,
                "cost" to it.cost
            )
        }

        val orderMap = mapOf(
            "_id" to order.id,
            "terminalId" to order.terminalId,
            "storeId" to order.storeId,
            "status" to order.status,
            "totalAmount" to order.totalAmount,
            "createdAt" to order.createdAt,
            "currency" to order.currency,
            "items" to orderItemsAsMaps
        )
        dittoRepository.upsert(Order.COLLECTION_NAME, orderMap)
    }
}
