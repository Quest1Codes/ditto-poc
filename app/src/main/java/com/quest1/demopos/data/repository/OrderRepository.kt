package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.orders.Order
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
                Order.fromDocument(docMap)
            }
        }
    }

    /**
     * Inserts or updates an Order in the database.
     */
    suspend fun upsertOrder(order: Order) {
        dittoRepository.upsert(Order.COLLECTION_NAME, order.toDocument())
    }
}