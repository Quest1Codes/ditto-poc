package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.repository.DittoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShopItemsUseCase @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    fun execute(): Flow<List<Item>> {
        return dittoRepository.getAvailableItems()
    }
}