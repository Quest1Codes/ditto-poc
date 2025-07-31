package com.quest1.demopos.ui.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.orders.OrderItem
import com.quest1.demopos.domain.usecase.order.GetActiveOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

data class PaymentGatewayUiState(
    // Header
    val totalAmount: Double = 0.0,

    // Card Info
    val cardHolderName: String = "Noah Oliver",
    val cardLastFour: String = "4671",
    val cardBrand: String = "Mastercard",
    val lastUsedDate: String = "Fri, Jun 15 2021",
    val isCardSelected: Boolean = true,

    // Order Details - from the active order
    val orderItems: List<OrderItem> = emptyList(),
    val itemTotal: Double = 0.0,
    val taxes: Double = 0.0, // Added to store calculated tax
    val tax_percentage: Double = 0.075, // 7.5% Tax
    val processor: String = "stripe"
)

@HiltViewModel
class PaymentGatewayViewModel @Inject constructor(
    private val getActiveOrderUseCase: GetActiveOrderUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(PaymentGatewayUiState())
    val uiState: State<PaymentGatewayUiState> = _uiState

    init {
        viewModelScope.launch {
            // Fetch the active order to get the itemized list
            val activeOrder = getActiveOrderUseCase.execute().firstOrNull()
            activeOrder?.let { order ->
                // Calculate the subtotal from the items in the order
                val itemsSubtotal = order.items.sumOf { (it.cost.toDouble()) * it.quantity }
                val taxes = itemsSubtotal * uiState.value.tax_percentage
                val total = itemsSubtotal + taxes

                _uiState.value = _uiState.value.copy(
                    orderItems = order.items,
                    itemTotal = itemsSubtotal,
                    taxes = taxes,
                    totalAmount = total
                )
            }
        }
    }

    fun onCardSelectionChanged() {
        _uiState.value = _uiState.value.copy(isCardSelected = !_uiState.value.isCardSelected)
    }
}