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

/**
 * Use case to get a real-time stream of the current active order.
 */
class GetActiveOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    fun execute(): Flow<Order?> {
        return orderRepository.observeActiveOrder()
    }
}

/**
 * Use case to update an item's quantity in the active order.
 * This handles adding, updating, and removing items from the order and recalculating the total.
 */
class UpdateOrderItemQuantityUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val inventoryRepository: InventoryRepository,
    private val sessionManager: SessionManager // Injected to get the current terminal ID
) {
    suspend fun execute(itemId: String, change: Int) {
        // Get the current terminal ID from the session. Fail if no user is logged in.
        val terminalId = sessionManager.currentUserId.first()
            ?: throw IllegalStateException("User is not logged in, cannot modify order.")

        // Get the active order for THIS terminal.
        val activeOrder = orderRepository.observeActiveOrder().first()

        // Get the item details from inventory.
        val inventoryItem = inventoryRepository.getAvailableItems().first()
            .find { it.id == itemId }
            ?: return // Cannot proceed if the item doesn't exist.

        // Determine the updated order, either by creating a new one or modifying the existing one.
        val updatedOrder = if (activeOrder == null) {
            // If no active order exists for this terminal, create a new one.
            createNewOrder(terminalId, inventoryItem, change)
        } else {
            // If an order already exists, update its items.
            updateExistingOrder(activeOrder, inventoryItem, change)
        }

        // If the updated order has items, save it. Otherwise, if it's empty, we might want to delete it.
        // For simplicity, we'll save it. An empty order will just be overwritten next time.
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
            id = terminalId, // CRITICAL CHANGE: Use terminalId as the unique document _id
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
            // Item exists in the cart, update its quantity
            val existingItem = existingItems[itemIndex]
            val newQuantity = existingItem.quantity + quantityChange

            if (newQuantity > 0) {
                existingItems[itemIndex] = existingItem.copy(quantity = newQuantity)
            } else {
                existingItems.removeAt(itemIndex) // Remove if quantity drops to 0 or less
            }
        } else if (quantityChange > 0) {
            // Item is not in the cart, add it
            existingItems.add(OrderItem(
                itemId = item.id,
                name = item.name,
                quantity = quantityChange,
                cost = item.price.toInt()
            ))
        }

        // Recalculate the total amount and return the updated order
        val newTotal = existingItems.sumOf { it.cost.toDouble() * it.quantity }
        return order.copy(items = existingItems, totalAmount = newTotal)
    }
}