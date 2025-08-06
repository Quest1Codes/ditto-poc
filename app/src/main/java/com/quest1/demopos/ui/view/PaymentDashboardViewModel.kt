package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.domain.usecase.TransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PaymentDashboardUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class PaymentDashboardViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentDashboardUiState())
    val uiState: StateFlow<PaymentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        transactionUseCase.getTransactions()
            .onEach { transactions ->
                _uiState.update { currentState ->
                    currentState.copy(
                        transactions = transactions,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
