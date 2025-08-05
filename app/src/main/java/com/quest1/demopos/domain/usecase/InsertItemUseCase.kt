package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.repository.DittoRepository
import javax.inject.Inject

class InsertItemUseCase @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    suspend operator fun invoke(item: Item) {
        dittoRepository.insertItem(item)
    }
}