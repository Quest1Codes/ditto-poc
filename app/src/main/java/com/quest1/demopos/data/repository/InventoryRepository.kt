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
                        id = docMap["_id"].toString(),
                        itemId = docMap["itemId"] as String,
                        name = docMap["name"] as String,
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
            "_id" to item.id,
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