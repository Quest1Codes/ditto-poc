/*
 * File: app/src/main/java/com/quest1/demopos/domain/usecase/GetShopItemsUseCase.kt
 * Description: Updated to simplify the logic and remove hardcoded data.
 * - The `ShopItem` data class is no longer needed here; this logic will move to the ViewModel.
 * - The use case now focuses solely on fetching the available items from the repository.
 */
package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopItemsUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    // The use case now directly returns the Flow of Items from the repository.
    // The ViewModel will be responsible for mapping this to its specific UI state.
    fun execute(): Flow<List<Item>> {
        return inventoryRepository.getAvailableItems()
    }
}
