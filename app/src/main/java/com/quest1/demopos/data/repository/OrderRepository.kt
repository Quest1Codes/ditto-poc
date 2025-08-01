package com.quest1.demopos.data.repository


import android.util.Log // 1. Add Log import
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.OrderItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val dittoRepository: DittoRepository,
    private val sessionManager: SessionManager
) {
    // 2. Define a TAG for logging
    private val TAG = "OrderRepository"

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
        // This flow will re-trigger when the user logs in and the terminalId becomes available.
        return sessionManager.currentUserId.flatMapLatest { terminalId ->
            if (terminalId == null) {
                flowOf(null) // If no user is logged in, there's no active order.
            } else {
                // The query now looks for an order where the _id matches the current terminal's ID.
                val query = "SELECT * FROM ${Order.COLLECTION_NAME} WHERE _id = :terminalId"
                val arguments = mapOf("terminalId" to terminalId)

                dittoRepository.observeCollection(query, arguments).map { documents ->
                    documents.firstOrNull()?.let { docMap ->
                        try {
                            // Mapping logic remains the same
                            val itemsList = (docMap["items"] as? List<Map<String, Any?>>
                                ?: emptyList()).mapNotNull { itemMap ->
                                OrderItem(
                                    itemId = itemMap["itemId"] as String,
                                    name = itemMap["name"] as String,
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
                                createdAt = Date((docMap["createdAt"] as Number).toLong()),
                                currency = docMap["currency"] as String,
                                items = itemsList
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error mapping order document: $docMap", e)
                            null // Return null if mapping fails
                        }
                    }
                }
            }
        }
    }

    /**
     * Inserts a new Order into the database.
     */
    suspend fun saveOrder(order: Order) {
        val orderMap = convertOrderToMap(order)
        dittoRepository.upsert(Order.COLLECTION_NAME, orderMap)
    }

    /**
     * Updates an existing Order in the database.
     */
    suspend fun updateOrder(order: Order) {
        val orderMap = convertOrderToMap(order)
        dittoRepository.upsert(Order.COLLECTION_NAME, orderMap)
    }

    /**
     * Converts an Order data class to a Map for Ditto.
     */
    private fun convertOrderToMap(order: Order): Map<String, Any?> {
        val orderItemsAsMaps = order.items.map {
            mapOf(
                "itemId" to it.itemId,
                "name" to it.name,
                "quantity" to it.quantity,
                "cost" to it.cost
            )
        }
        return mapOf(
            "_id" to order.id,
            "terminalId" to order.terminalId,
            "storeId" to order.storeId,
            "status" to order.status,
            "totalAmount" to order.totalAmount,
            "createdAt" to order.createdAt.time, // Convert Date to Long for storage
            "currency" to order.currency,
            "items" to orderItemsAsMaps
        )
    }

    suspend fun deleteOrder(orderId: String) {
        val query = "DELETE FROM ${Order.COLLECTION_NAME} WHERE _id = :orderId"
        val arguments = mapOf("orderId" to orderId)
        dittoRepository.executeQuery(query, arguments)
    }
}