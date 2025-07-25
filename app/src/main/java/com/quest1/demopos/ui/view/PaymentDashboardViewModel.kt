package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.payment.PaymentCard
import com.quest1.demopos.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PaymentDashboardUiState(
    val paymentCards: List<PaymentCard> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class PaymentDashboardViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentDashboardUiState())
    val uiState: StateFlow<PaymentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadPaymentCards()
    }

    private fun loadPaymentCards() {
        paymentRepository.getPaymentCards()
            .onEach { paymentCards ->
                _uiState.update { currentState ->
                    currentState.copy(
                        paymentCards = paymentCards,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun filterByStatus(status: String) {
        _uiState.update { it.copy(isLoading = true) }

        paymentRepository.getPaymentCardsByStatus(status)
            .onEach { paymentCards ->
                _uiState.update { currentState ->
                    currentState.copy(
                        paymentCards = paymentCards,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadAllPaymentCards() {
        _uiState.update { it.copy(isLoading = true) }
        loadPaymentCards()
    }
}
