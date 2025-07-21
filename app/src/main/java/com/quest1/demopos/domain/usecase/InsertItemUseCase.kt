/*
 * File: app/src/main/java/com/quest1/demopos/domain/usecase/InsertItemUseCase.kt
 * Description: A new use case for inserting an item into the database.
 * - This follows your app's architecture by separating the business logic (inserting an item)
 * from the ViewModel.
 * - It takes an `Item` and passes it to the repository.
 */
package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.repository.InventoryRepository
import javax.inject.Inject

class InsertItemUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(item: Item) {
        inventoryRepository.insertItem(item)
    }
}
