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
    val totalAmount: Double = 374.59,

    // Card Info
    val cardHolderName: String = "Noah Oliver",
    val cardLastFour: String = "4671", // Updated to match new image
    val cardBrand: String = "Mastercard",
    val lastUsedDate: String = "Fri, Jun 15 2021", // Added for new UI
    val isCardSelected: Boolean = true,
    // Order Details - from the active order
    val orderItems: List<OrderItem> = emptyList(),
    val itemTotal: Double = 0.0,

    // Order Details - hardcoded values to match the design
    val servicePrice: Double = 364.59,
    val bookingFee: Double = 1.99,
    val waitlistingFee: Double = 2.99,
    val discount: Double = 1430.00,
    val taxes: Double = 179.24,
    val currency: String = "USD",
    val cardType: String = "Debit Card",
    val issuedBy: String = "Bank of America",
    val transactionType: String = "Online",
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
                // Calculate the total from the items in the order
                val itemsSubtotal = order.items.sumOf { (it.cost.toDouble()) * it.quantity }

                // For the demo, we'll use the hardcoded fees from the screenshot to calculate the final total
                val total = _uiState.value.servicePrice + _uiState.value.bookingFee + _uiState.value.waitlistingFee

                _uiState.value = _uiState.value.copy(
                    orderItems = order.items,
                    itemTotal = itemsSubtotal,
                    totalAmount = total
                )
            }
        }
    }

    fun onCardSelectionChanged() {
        _uiState.value = _uiState.value.copy(isCardSelected = !_uiState.value.isCardSelected)
    }
}