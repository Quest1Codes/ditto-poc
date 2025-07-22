package com.quest1.demopos.domain.usecase.order

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.OrderItem
import com.quest1.demopos.data.repository.InventoryRepository
import com.quest1.demopos.data.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject

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
    private val inventoryRepository: InventoryRepository // Needed to get item details
) {
    suspend fun execute(itemId: String, change: Int) {
        // 1. Get the current active order, or determine if a new one is needed.
        var activeOrder = orderRepository.observeActiveOrder().firstOrNull()
        val isNewOrder = activeOrder == null

        if (isNewOrder) {
            val newOrderId = UUID.randomUUID().toString()
            activeOrder = Order(
                id = newOrderId,
                terminalId = "term_A1", // Example value
                storeId = "store_01",   // Example value
                status = Order.STATUS_PENDING,
                totalAmount = 0.0,
                createdAt = System.currentTimeMillis(),
                currency = "USD",
                items = emptyList()
            )
        }

        // 2. Find the inventory item to get its price and name.
        val inventoryItem = inventoryRepository.getAvailableItems().firstOrNull()?.find { it.id == itemId }
            ?: return // Cannot proceed if the item doesn't exist in inventory

        // 3. Update the items list within the order.
        val existingOrderItem = activeOrder!!.items.find { it.itemId == itemId }
        val currentQuantity = existingOrderItem?.quantity ?: 0
        val newQuantity = (currentQuantity + change).coerceAtLeast(0)

        val updatedItems = activeOrder.items.toMutableList()

        if (newQuantity == 0) {
            // Remove the item from the order
            updatedItems.removeAll { it.itemId == itemId }
        } else {
            if (existingOrderItem != null) {
                // Update quantity of existing item
                val itemIndex = updatedItems.indexOf(existingOrderItem)
                updatedItems[itemIndex] = existingOrderItem.copy(quantity = newQuantity)
            } else {
                // Add new item to the order
                updatedItems.add(OrderItem(
                    itemId = inventoryItem.id,
                    name = inventoryItem.name,
                    quantity = newQuantity,
                    cost = inventoryItem.price.toInt() // Assuming price can be converted
                ))
            }
        }

        // 4. Recalculate the total amount.
        val newTotal = updatedItems.sumOf { (it.cost.toDouble()) * it.quantity }

        // 5. Save or Update the order back into the database based on whether it was new.
        val finalOrder = activeOrder.copy(items = updatedItems, totalAmount = newTotal)
        if (isNewOrder) {
            orderRepository.saveOrder(finalOrder)
        } else {
            orderRepository.updateOrder(finalOrder)
        }
    }
}