package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.StorePerformance
import com.quest1.demopos.data.model.analytics.Transaction
import com.quest1.demopos.data.repository.AnalyticsRepository
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
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        combine(
            analyticsRepository.getStorePerformance(),
            analyticsRepository.getRecentTransactions(),
            analyticsRepository.getAcquirerRankings()
        ) { performance, transactions, acquirers ->
            println(performance)
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
