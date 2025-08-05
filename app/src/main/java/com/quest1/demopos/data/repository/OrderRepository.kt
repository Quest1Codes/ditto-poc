package com.quest1.demopos.data.repository


import android.util.Log
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
    private val TAG = "OrderRepository"

    init {
        val subscriptionQuery = "SELECT * FROM ${Order.COLLECTION_NAME}"
        dittoRepository.startSubscription(subscriptionQuery)
    }

    fun observeActiveOrder(): Flow<Order?> {
        return sessionManager.currentUserId.flatMapLatest { terminalId ->
            if (terminalId == null) {
                flowOf(null)
            } else {
                val query = "SELECT * FROM ${Order.COLLECTION_NAME} WHERE _id = :terminalId"
                val arguments = mapOf("terminalId" to terminalId)

                dittoRepository.observeCollection(query, arguments).map { documents ->
                    documents.firstOrNull()?.let { docMap ->
                        try {
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
                            null
                        }
                    }
                }
            }
        }
    }

    suspend fun saveOrder(order: Order) {
        val orderMap = convertOrderToMap(order)
        dittoRepository.upsert(Order.COLLECTION_NAME, orderMap)
    }

    suspend fun updateOrder(order: Order) {
        val orderMap = convertOrderToMap(order)
        dittoRepository.upsert(Order.COLLECTION_NAME, orderMap)
    }

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
            "createdAt" to order.createdAt.time,
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