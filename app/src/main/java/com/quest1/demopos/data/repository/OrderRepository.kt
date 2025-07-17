package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.OrderItem
import com.quest1.demopos.data.model.orders.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor() {

    // --- Stub Data ---
    private val stubOrders = listOf(
        Order(
            id = "ord_1001",
            terminalId = "term_A1",
            storeId = "store_01",
            status = "COMPLETED",
            createdAt = System.currentTimeMillis() - 5 * 60 * 1000, // 5 minutes ago
            totalAmount = 45.50,
            currency = "USD",
            items = listOf(
                OrderItem(itemId = "item_burger", name = "Classic Burger", quantity = 1, cost = 15),
                OrderItem(itemId = "item_fries", name = "Large Fries", quantity = 1, cost = 5),
            )
        ),
        Order(
            id = "ord_1002",
            terminalId = "term_A1",
            storeId = "store_01",
            status = "PENDING",
            createdAt = System.currentTimeMillis(), // Now
            totalAmount = 12.00,
            currency = "USD",
            items = listOf(
                OrderItem(itemId = "item_coffee", name = "Latte", quantity = 2, cost = 6)
            )
        )
    )

    private val stubTransactions = listOf(
        Transaction(
            id = "txn_01",
            transactionId = UUID.randomUUID().toString(),
            gatewayId = "gw_stripe",
            status = "SUCCESS",
            completedAt = System.currentTimeMillis() - 5 * 60 * 1000,
            failureReason = null
        )
    )

    // --- Repository Functions ---

    fun getOrdersForStore(storeId: String): Flow<List<Order>> = flow {
        emit(stubOrders.filter { it.storeId == storeId })
    }

    fun getOrderDetails(orderId: String): Flow<Order?> = flow {
        emit(stubOrders.find { it.id == orderId })
    }

    fun getTransactionForOrder(orderId: String): Flow<Transaction?> = flow {
        // This is a simplified lookup. In reality, an order would have a transactionId.
        emit(stubTransactions.firstOrNull())
    }
}