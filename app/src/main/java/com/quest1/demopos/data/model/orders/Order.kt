package com.quest1.demopos.data.model.orders

import android.util.Log
import live.ditto.ditto_wrapper.DittoProperty
import live.ditto.ditto_wrapper.MissingPropertyException
import live.ditto.ditto_wrapper.deserializeProperty
import java.util.Date

data class Order(
    val id: String,
    val terminalId: String,
    val storeId: String,
    val status: String,
    val createdAt: Date,
    val totalAmount: Double,
    val currency: String,
    val items: List<OrderItem>
) {
    fun serializeAsMap(): Map<String, Any?> {
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

    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_COMPLETED = "COMPLETED"
    }
}

fun DittoProperty.toOrder(): Order {
    return try {
        val itemsList = (this["items"] as? List<Map<String, Any?>> ?: emptyList()).mapNotNull { itemMap ->
            OrderItem(
                itemId = itemMap["itemId"] as String,
                name = itemMap["name"] as String,
                quantity = (itemMap["quantity"] as Number).toInt(),
                cost = (itemMap["cost"] as Number).toInt()
            )
        }
        Order(
            id = deserializeProperty("_id"),
            terminalId = deserializeProperty("terminalId"),
            storeId = deserializeProperty("storeId"),
            status = deserializeProperty("status"),
            totalAmount = (deserializeProperty<Number>("totalAmount")).toDouble(),
            createdAt = Date((deserializeProperty<Number>("createdAt")).toLong()),
            currency = deserializeProperty("currency"),
            items = itemsList
        )
    } catch (e: Exception) {
        Log.e("Order.kt", "Error mapping document: $this", e)
        throw MissingPropertyException("Error deserializing Order", this)
    }
}