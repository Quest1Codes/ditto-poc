package com.example.quest1pos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quest1pos.domain.usecase.GetShopItemsUseCase
import com.example.quest1pos.domain.usecase.ShopItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ShopUiState(
    val items: List<ShopItem> = emptyList(),
    val cartItemCount: Int = 0,
    val cartTotal: Double = 0.0,
    val isLoading: Boolean = true
) {
    // Computed property to get only items that are in the cart
    val itemsInCart: List<ShopItem>
        get() = items.filter { it.quantityInCart > 0 }
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getShopItemsUseCase: GetShopItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init {
        loadShopItems()
    }

    private fun loadShopItems() {
        // This check prevents reloading data if the ViewModel is preserved across navigation
        if (_uiState.value.items.isEmpty()) {
            getShopItemsUseCase.execute().onEach { shopItems ->
                _uiState.update { currentState ->
                    val total = shopItems.sumOf { it.item.price * it.quantityInCart }
                    val count = shopItems.sumOf { it.quantityInCart }
                    currentState.copy(
                        items = shopItems,
                        cartTotal = total,
                        cartItemCount = count,
                        isLoading = false
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

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
            val total = newItems.sumOf { it.item.price * it.quantityInCart }
            val count = newItems.sumOf { it.quantityInCart }
            currentState.copy(items = newItems, cartTotal = total, cartItemCount = count)
        }
    }

    fun removeItemFromCart(itemId: String) {
        _uiState.update { currentState ->
            val newItems = currentState.items.map { shopItem ->
                if (shopItem.item.id == itemId) {
                    shopItem.copy(quantityInCart = 0) // Set quantity to 0 to remove
                } else {
                    shopItem
                }
            }
            val total = newItems.sumOf { it.item.price * it.quantityInCart }
            val count = newItems.sumOf { it.quantityInCart }
            currentState.copy(items = newItems, cartTotal = total, cartItemCount = count)
        }
    }
}