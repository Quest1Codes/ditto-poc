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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

data class PaymentGatewayUiState(
    val totalAmount: Double = 0.0,
    val cardHolderName: String = "John Doe",
    val cardLastFour: String = "0000",
    val cardBrand: String = "Mastercard",
    val lastUsedDate: String = "",
    val isCardSelected: Boolean = true,
    val orderItems: List<OrderItem> = emptyList(),
    val itemTotal: Double = 0.0,
    val taxes: Double = 0.0,
    val tax_percentage: Double = 0.075,
    val processor: String = "stripe",
    val isLoadingGateway: Boolean = true
)

@HiltViewModel
class PaymentGatewayViewModel @Inject constructor(
    private val getActiveOrderUseCase: GetActiveOrderUseCase,
    private val getOptimalGatewayUseCase: GetOptimalGatewayUseCase
) : ViewModel() {
    private val _uiState = mutableStateOf(PaymentGatewayUiState())
    val uiState: State<PaymentGatewayUiState> = _uiState

    init {
        val randomCardLastFour = Random.nextInt(1000, 10000).toString()

        val calendar = Calendar.getInstance()
        val daysToSubtract = Random.nextInt(2, 6)
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract)
        val dateFormat = SimpleDateFormat("E, MMM d yyyy", Locale.getDefault())
        val randomPastDate = dateFormat.format(calendar.time)

        _uiState.value = _uiState.value.copy(
            cardLastFour = randomCardLastFour,
            lastUsedDate = randomPastDate
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingGateway = true)

            val activeOrder = getActiveOrderUseCase.execute().firstOrNull()
            val optimalProcessor = getOptimalGatewayUseCase.execute()

            activeOrder?.let { order ->
                val itemsSubtotal = order.items.sumOf { (it.cost.toDouble()) * it.quantity }
                val taxes = itemsSubtotal * _uiState.value.tax_percentage
                val total = itemsSubtotal + taxes
                _uiState.value = _uiState.value.copy(
                    orderItems = order.items,
                    itemTotal = itemsSubtotal,
                    taxes = taxes,
                    totalAmount = total,
                    processor = optimalProcessor,
                    isLoadingGateway = false
                )
            } ?: run {
                _uiState.value = _uiState.value.copy(
                    processor = optimalProcessor,
                    isLoadingGateway = false
                )
            }
        }
    }

    fun onCardSelectionChanged() {
        _uiState.value = _uiState.value.copy(isCardSelected = !_uiState.value.isCardSelected)
    }
}