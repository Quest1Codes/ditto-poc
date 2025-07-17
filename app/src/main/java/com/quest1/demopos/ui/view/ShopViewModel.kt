package com.quest1.demopos.ui.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.SampleData
import com.quest1.demopos.domain.usecase.GetSampleDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

// Wrapper class to hold an item and its quantity in the cart
data class ShopItemState(
    val item: SampleData,
    val quantityInCart: Int = 0
)

// Restore the full UI state to support the cart
data class ShopUiState(
    val items: List<ShopItemState> = emptyList(),
    val cartItemCount: Int = 0,
    val cartTotal: Double = 0.0,
    val isLoading: Boolean = true
) {
    val itemsInCart: List<ShopItemState>
        get() = items.filter { it.quantityInCart > 0 }
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getSampleDataUseCase: GetSampleDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init {
        loadDittoData()
    }

    private fun loadDittoData() {
        getSampleDataUseCase()
            .onEach { sampleItems ->
                _uiState.update {
                    // Map the incoming data to our UI state wrapper
                    val shopItems = sampleItems.map { ShopItemState(item = it) }
                    it.copy(items = shopItems, isLoading = false)
                }
            }
            .catch { e -> Log.e("ShopViewModel", "Error loading data", e) }
            .launchIn(viewModelScope)
    }

    // Restore the cart management functions
    fun updateQuantity(itemId: String, change: Int) {
        _uiState.update { currentState ->
            val newItems = currentState.items.map { shopItem ->
                if (shopItem.item.id == itemId) {
                    val newQuantity = (shopItem.quantityInCart + change).coerceAtLeast(0)
                    shopItem.copy(quantityInCart = newQuantity)
                } else {
                    shopItem
                }
            }
            // Recalculate totals
            val total = newItems.sumOf { (it.item.price ?: 0.0) * it.quantityInCart }
            val count = newItems.sumOf { it.quantityInCart }
            currentState.copy(items = newItems, cartTotal = total, cartItemCount = count)
        }
    }

    fun removeItemFromCart(itemId: String) {
        // Removing is the same as setting quantity to 0
        updateQuantity(itemId, Int.MIN_VALUE)
    }
}