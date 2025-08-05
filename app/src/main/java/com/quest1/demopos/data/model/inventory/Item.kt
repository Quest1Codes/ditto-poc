package com.quest1.demopos.data.model.inventory

import live.ditto.ditto_wrapper.DittoProperty
import live.ditto.ditto_wrapper.MissingPropertyException
import live.ditto.ditto_wrapper.deserializeProperty

data class Item(
    val id: String,
    val itemId: String,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val sku: String
) {
    fun serializeAsMap(): Map<String, Any?> {
        return mapOf(
            "_id" to id,
            "itemId" to itemId,
            "name" to name,
            "price" to price,
            "description" to description,
            "category" to category,
            "sku" to sku
        )
    }
}

fun DittoProperty.toItem(): Item {
    return try {
        Item(
            id = deserializeProperty("_id"),
            itemId = deserializeProperty("itemId"),
            name = deserializeProperty("name"),
            price = (deserializeProperty<Number>("price")).toDouble(),
            description = deserializeProperty("description"),
            category = deserializeProperty("category"),
            sku = deserializeProperty("sku")
        )
    } catch (e: Exception) {
        throw MissingPropertyException("Error deserializing Item: $this", this)
    }
}