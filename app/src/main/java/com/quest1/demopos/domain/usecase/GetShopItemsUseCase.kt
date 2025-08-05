package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopItemsUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    fun execute(): Flow<List<Item>> {
        return inventoryRepository.getAvailableItems()
    }
}
