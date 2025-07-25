package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.StorePerformance
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.repository.AnalyticsRepository
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
    private val analyticsRepository: AnalyticsRepository,
    private val transactionUseCase: TransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        combine(
            transactionUseCase.getStorePerformance(),
            transactionUseCase.getRecentTransactions(),
            analyticsRepository.getAcquirerRankings()
        ) { performance, transactions, acquirers ->
            println(transactions)
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
