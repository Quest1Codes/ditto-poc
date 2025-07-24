package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.OrderItem
import com.quest1.demopos.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

enum class PaymentStatus {
    INITIATING,
    PROCESSING,
    SUCCESSFUL,
    FAILED,
    REDIRECTING
}

data class PaymentUiState(
    val status: PaymentStatus = PaymentStatus.INITIATING,
    val progress: Float = 0f
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val orderRepository: OrderRepository // Injected the repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    /**
     * Starts the payment process, accepting the current cart state to create an order on success.
     * @param itemsInCart The list of items from the cart.
     * @param cartTotal The total amount for the order.
     */
    fun startPaymentProcess(itemsInCart: List<ShopItemState>, cartTotal: Double) {
        viewModelScope.launch {
            // Stage 1: Initiating
            _uiState.update { it.copy(status = PaymentStatus.INITIATING) }
            delay(2000) // Simulate redirection time

            // Stage 2: Processing
            _uiState.update { it.copy(status = PaymentStatus.PROCESSING) }
            // Simulate processing progress
            for (i in 1..10) {
                delay(300)
                _uiState.update { it.copy(progress = i / 10f) }
            }

            // Stage 3: Success or Failure (50/50 chance for demo)
            val isSuccess = Random.nextBoolean()
            if (isSuccess) {
                _uiState.update { it.copy(status = PaymentStatus.SUCCESSFUL) }

                // --- Create and save the order after successful payment ---
                try {
                    // Map ShopItemState to the OrderItem data model
                    val orderItems = itemsInCart.map { cartItem ->
                        OrderItem(
                            itemId = cartItem.item.itemId,
                            name = cartItem.item.name,
                            quantity = cartItem.quantityInCart,
                            // Assuming cost is in cents, convert price (Double) to Int
                            cost = (cartItem.item.price * 100).toInt()
                        )
                    }

                    // Create the final Order object
                    val newOrder = Order(
                        id = UUID.randomUUID().toString(),
                        terminalId = "terminal_A1", // Example data, replace with actual
                        storeId = "store_01",       // Example data, replace with actual
                        status = Order.STATUS_COMPLETED,
                        createdAt = Date(),
                        totalAmount = cartTotal,
                        currency = "USD",
                        items = orderItems
                    )
                    // Persist the order to Ditto, which will then sync to the cloud
                    orderRepository.upsertOrder(newOrder)
                } catch (e: Exception) {
                    // Handle potential errors during order creation
                }
                // -----------------------------------------------------------

            } else {
                _uiState.update { it.copy(status = PaymentStatus.FAILED) }
            }
        }
    }

    fun startRedirectHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = PaymentStatus.REDIRECTING) }
            delay(3000) // Simulate redirect delay
            // In a real app, you would navigate home here via the UI.
        }
    }

    fun reset() {
        _uiState.value = PaymentUiState()
    }
}