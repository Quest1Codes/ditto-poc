package com.quest1.demopos.ui.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.repository.InventoryRepository
import com.quest1.demopos.domain.usecase.GetShopItemsUseCase
import com.quest1.demopos.domain.usecase.InsertItemUseCase
import com.quest1.demopos.domain.usecase.order.GetActiveOrderUseCase
import com.quest1.demopos.domain.usecase.order.UpdateOrderItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import com.quest1.demopos.data.sampleItems

// Represents an inventory item and its quantity in the cart.
data class ShopItemState(
    val item: Item,
    val quantityInCart: Int = 0
)

data class ShopUiState(
    val items: List<ShopItemState> = emptyList(),
    val cartItemCount: Int = 0,
    val cartTotal: Double = 0.0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val itemsInCart: List<ShopItemState>
        get() = items.filter { it.quantityInCart > 0 }
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val getShopItemsUseCase: GetShopItemsUseCase,
    private val insertItemUseCase: InsertItemUseCase,
    private val getActiveOrderUseCase: GetActiveOrderUseCase,
    private val updateOrderItemQuantityUseCase: UpdateOrderItemQuantityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init {
        checkAndSeedInitialData()
        observeInventoryAndActiveOrder()
    }


    private fun checkAndSeedInitialData() {
        viewModelScope.launch {
            val initialInventory = getShopItemsUseCase.execute().first()
            if (initialInventory.isEmpty()) {
//                addSampleItemsForTesting()
            }
        }
    }

    private fun observeInventoryAndActiveOrder() {
        viewModelScope.launch {
            val inventoryFlow = getShopItemsUseCase.execute()
            val activeOrderFlow = getActiveOrderUseCase.execute()

            inventoryFlow.combine(activeOrderFlow) { inventoryItems, activeOrder ->
                val orderItemMap = activeOrder?.items?.associate { it.itemId to it.quantity } ?: emptyMap()

                val shopItems = inventoryItems.map { item ->
                    ShopItemState(
                        item = item,
                        quantityInCart = orderItemMap[item.id] ?: 0
                    )
                }

                val total = activeOrder?.totalAmount ?: 0.0
                val count = activeOrder?.items?.sumOf { it.quantity } ?: 0

                ShopUiState(
                    items = shopItems,
                    isLoading = false,
                    cartTotal = total,
                    cartItemCount = count
                )
            }
                .catch { e -> Log.e("ShopViewModel", "Error combining flows", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "An error occurred while loading your shop data."
                    )
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    private fun addSampleItemsForTesting() {
        viewModelScope.launch {
            Log.d("ShopViewModel", "Database is empty. Adding sample items for testing...")
            sampleItems.forEach { insertItemUseCase(it) }
        }
    }

    fun updateQuantity(itemId: String, change: Int) {
        viewModelScope.launch {
            try {
                updateOrderItemQuantityUseCase.execute(itemId, change)
            } catch (e: Exception) {
                Log.e("ShopViewModel", "Failed to update quantity for item: $itemId", e)
            }
        }
    }

    fun removeItemFromCart(itemId: String) {
        updateQuantity(itemId, Int.MIN_VALUE)
    }
}