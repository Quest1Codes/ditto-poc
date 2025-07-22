/*
 * File: app/src/main/java/com/quest1/demopos/data/repository/InventoryRepository.kt
 * Description: Corrected a ClassCastException when mapping the price field.
 * - The code now safely handles numeric types from Ditto by casting to `Number`
 * and then converting to `Double`.
 */
package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.inventory.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log


@Singleton
class InventoryRepository @Inject constructor(
    private val dittoRepository: DittoRepository
) {

    init {
        val subscriptionQuery = "SELECT * FROM ${Item.COLLECTION_NAME}"
        dittoRepository.startSubscription(subscriptionQuery)
    }

    fun getAvailableItems(): Flow<List<Item>> {
        val query = "SELECT * FROM ${Item.COLLECTION_NAME}"
        return dittoRepository.observeCollection(query).map { documents ->
            documents.mapNotNull { docMap ->
                try {
                    Item(
                        // The primary key from Ditto is always `_id`
                        id = docMap["_id"].toString(),
                        itemId = docMap["itemId"] as String,
                        name = docMap["name"] as String,
                        // CORRECTED LINE: Safely convert any number type to Double.
                        price = (docMap["price"] as Number).toDouble(),
                        description = docMap["description"] as String,
                        category = docMap["category"] as String,
                        sku = docMap["sku"] as String
                    )
                } catch (e: Exception) {
                    Log.e("InventoryRepository", "Error mapping document: $docMap", e)
                    null
                }
            }
        }
    }

    suspend fun insertItem(item: Item) {
        val itemMap = mapOf(
            "_id" to item.id, // Using _id is best practice for the primary key
            "itemId" to item.itemId,
            "name" to item.name,
            "price" to item.price,
            "description" to item.description,
            "category" to item.category,
            "sku" to item.sku
        )
        dittoRepository.upsert(Item.COLLECTION_NAME, itemMap)
    }
}
