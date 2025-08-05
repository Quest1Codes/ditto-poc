package com.quest1.demopos.domain.usecase.order

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.OrderItem
import com.quest1.demopos.data.repository.InventoryRepository
import com.quest1.demopos.data.repository.OrderRepository
import com.quest1.demopos.data.repository.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject
import java.util.Date

// Use case to get a real-time stream of the current active order.
class GetActiveOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    fun execute(): Flow<Order?> {
        return orderRepository.observeActiveOrder()
    }
}

// Handles adding, updating, and removing items from the active order.
class UpdateOrderItemQuantityUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val inventoryRepository: InventoryRepository,
    private val sessionManager: SessionManager
) {
    suspend fun execute(itemId: String, change: Int) {
        val terminalId = sessionManager.currentUserId.first()
            ?: throw IllegalStateException("User is not logged in, cannot modify order.")

        val activeOrder = orderRepository.observeActiveOrder().first()

        val inventoryItem = inventoryRepository.getAvailableItems().first()
            .find { it.id == itemId }
            ?: return

        val updatedOrder = if (activeOrder == null) {
            createNewOrder(terminalId, inventoryItem, change)
        } else {
            updateExistingOrder(activeOrder, inventoryItem, change)
        }

        if (updatedOrder != null) {
            orderRepository.saveOrder(updatedOrder)
        }
    }

    private fun createNewOrder(terminalId: String, item: Item, quantityChange: Int): Order? {
        // Don't create a new order if the first action is to decrease quantity.
        if (quantityChange <= 0) return null

        val newOrderItem = OrderItem(
            itemId = item.id,
            name = item.name,
            quantity = quantityChange,
            cost = item.price.toInt() // Assuming price can be safely converted to Int
        )

        return Order(
            id = terminalId, // Use terminalId as the unique document _id for the active order
            terminalId = terminalId,
            storeId = "store_01",   // TODO: Replace with actual store ID from session or config
            status = Order.STATUS_PENDING,
            totalAmount = newOrderItem.cost * newOrderItem.quantity.toDouble(),
            createdAt = Date(),
            currency = "USD",
            items = listOf(newOrderItem)
        )
    }

    private fun updateExistingOrder(order: Order, item: Item, quantityChange: Int): Order {
        val existingItems = order.items.toMutableList()
        val itemIndex = existingItems.indexOfFirst { it.itemId == item.id }

        if (itemIndex != -1) {
            val existingItem = existingItems[itemIndex]
            val newQuantity = existingItem.quantity + quantityChange

            if (newQuantity > 0) {
                existingItems[itemIndex] = existingItem.copy(quantity = newQuantity)
            } else {
                existingItems.removeAt(itemIndex)
            }
        } else if (quantityChange > 0) {
            existingItems.add(OrderItem(
                itemId = item.id,
                name = item.name,
                quantity = quantityChange,
                cost = item.price.toInt()
            ))
        }

        val newTotal = existingItems.sumOf { it.cost.toDouble() * it.quantity }
        return order.copy(items = existingItems, totalAmount = newTotal)
    }
}