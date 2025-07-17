package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// A simple data class to combine an Item with its quantity in the cart
data class ShopItem(
    val item: Item,
    val quantityInCart: Int
)

class GetShopItemsUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
    // In a real app, you'd inject a CartRepository here to get quantities
) {
    fun execute(): Flow<List<ShopItem>> {
        val cart = mapOf("item_laptop_stand" to 1)

        return inventoryRepository.getAvailableItems().map { items ->
            items.map { item ->
                ShopItem(
                    item = item,
                    quantityInCart = cart[item.id] ?: 0
                )
            }
        }
    }
}