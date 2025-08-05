package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.StorePerformance
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.domain.usecase.TransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class AnalyticsUiState(
    val storePerformance: StorePerformance? = null,
    val recentTransactions: List<Transaction> = emptyList(),
    val acquirers: List<Acquirer> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase // The only dependency needed
) : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        // Combine all data flows from the single use case
        combine(
            transactionUseCase.getStorePerformance(),
            transactionUseCase.getRecentTransactions(),
            transactionUseCase.getAcquirerRankings()
        ) { performance, transactions, acquirers ->
            AnalyticsUiState(
                storePerformance = performance,
                recentTransactions = transactions,
                acquirers = acquirers,
                isLoading = false
            )
        }.onEach { newState -> _uiState.value = newState }
            .launchIn(viewModelScope)
    }
}