package com.quest1.demopos.domain.usecase.order

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.OrderItem
import com.quest1.demopos.data.repository.DittoRepository
import com.quest1.demopos.data.repository.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.*
import javax.inject.Inject

class UpdateOrderItemQuantityUseCase @Inject constructor(
    private val dittoRepository: DittoRepository,
    private val sessionManager: SessionManager,
    private val getActiveOrderUseCase: GetActiveOrderUseCase
) {
    suspend fun execute(itemId: String, change: Int) {
        val terminalId = sessionManager.currentUserId.first()
            ?: throw IllegalStateException("User is not logged in, cannot modify order.")

        val inventoryItem = dittoRepository.getItemById(itemId) ?: return

        val activeOrder = getActiveOrderUseCase.execute().firstOrNull()

        val updatedOrder = if (activeOrder == null) {
            createNewOrder(terminalId, inventoryItem, change)
        } else {
            updateExistingOrder(activeOrder, inventoryItem, change)
        }

        if (updatedOrder != null) {
            if (updatedOrder.items.isEmpty()) {
                dittoRepository.deleteOrder(updatedOrder.id)
                sessionManager.clearActiveOrderId()
            } else {
                dittoRepository.saveOrder(updatedOrder)
            }
        }
    }

    private suspend fun createNewOrder(terminalId: String, item: Item, quantityChange: Int): Order? {
        if (quantityChange <= 0) return null

        val newOrderItem = OrderItem(
            itemId = item.id,
            name = item.name,
            quantity = quantityChange,
            cost = item.price.toInt()
        )

        // Create the order with a new, permanent UUID from the start.
        val newOrder = Order(
            id = UUID.randomUUID().toString(),
            terminalId = terminalId,
            storeId = "store_01",
            status = Order.STATUS_PENDING,
            totalAmount = newOrderItem.cost * newOrderItem.quantity.toDouble(),
            createdAt = Date(),
            currency = "USD",
            items = listOf(newOrderItem)
        )
        // Set this new order as the active one in the session.
        sessionManager.setActiveOrderId(newOrder.id)
        return newOrder
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
            existingItems.add(
                OrderItem(
                    itemId = item.id,
                    name = item.name,
                    quantity = quantityChange,
                    cost = item.price.toInt()
                )
            )
        }

        val newTotal = existingItems.sumOf { it.cost.toDouble() * it.quantity }
        return order.copy(items = existingItems, totalAmount = newTotal)
    }
}