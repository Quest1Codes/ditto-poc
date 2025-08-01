package com.quest1.demopos.ui.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.orders.OrderItem
import com.quest1.demopos.domain.usecase.GetOptimalGatewayUseCase
import com.quest1.demopos.domain.usecase.order.GetActiveOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentGatewayUiState(
    val totalAmount: Double = 0.0,

    val cardHolderName: String = "John Doe",
    val cardLastFour: String = "4671",
    val cardBrand: String = "Mastercard",
    val lastUsedDate: String = "Fri, Jun 15 2021",
    val isCardSelected: Boolean = true,

    val orderItems: List<OrderItem> = emptyList(),
    val itemTotal: Double = 0.0,
    val taxes: Double = 0.0,
    val tax_percentage: Double = 0.075,
    val processor: String = "stripe"
)

@HiltViewModel
class PaymentGatewayViewModel @Inject constructor(
    private val getActiveOrderUseCase: GetActiveOrderUseCase,
    private val getOptimalGatewayUseCase: GetOptimalGatewayUseCase // Inject the new use case
) : ViewModel() {

    private val _uiState = mutableStateOf(PaymentGatewayUiState())
    val uiState: State<PaymentGatewayUiState> = _uiState

    init {
        viewModelScope.launch {
            // Fetch the active order to get the itemized list
            val activeOrder = getActiveOrderUseCase.execute().firstOrNull()

            // Fetch the best performing payment gateway
            val optimalProcessor = getOptimalGatewayUseCase.execute()

            activeOrder?.let { order ->
                // Calculate the subtotal from the items in the order
                val itemsSubtotal = order.items.sumOf { (it.cost.toDouble()) * it.quantity }
                val taxes = itemsSubtotal * uiState.value.tax_percentage
                val total = itemsSubtotal + taxes

                _uiState.value = _uiState.value.copy(
                    orderItems = order.items,
                    itemTotal = itemsSubtotal,
                    taxes = taxes,
                    totalAmount = total,
                    processor = optimalProcessor // Set the processor dynamically
                )
            } ?: run {
                // Handle case where there's no active order but we still need to set the processor
                _uiState.value = _uiState.value.copy(processor = optimalProcessor)
            }
        }
    }

    fun onCardSelectionChanged() {
        _uiState.value = _uiState.value.copy(isCardSelected = !_uiState.value.isCardSelected)
    }
}