package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.StorePerformance
import com.quest1.demopos.data.model.analytics.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor() {

    private val mockAcquirers = listOf(
        Acquirer(1, "Acquirer A", "healthy", "120ms", "99.8%"),
        Acquirer(2, "Acquirer B", "healthy", "150ms", "99.5%"),
        Acquirer(3, "Acquirer C", "degraded", "250ms", "98.2%"),
        Acquirer(4, "Acquirer D", "failing", "500ms", "95.1%")
    )

    private val recentTransactions = listOf(
        Transaction("TXN001", "$45.99", "2 mins ago", "success"),
        Transaction("TXN002", "$120.50", "5 mins ago", "success"),
        Transaction("TXN003", "$78.25", "8 mins ago", "failed"),
        Transaction("TXN004", "$200.00", "12 mins ago", "success")
    )

    private val storePerformance = StorePerformance(
        totalTransactions = 247,
        transactionGrowth = "+12% from yesterday",
        revenueToday = "$12,450",
        revenueGrowth = "+8% from yesterday"
    )

    fun getStorePerformance(): Flow<StorePerformance> = flow {
        emit(storePerformance)
    }

    fun getRecentTransactions(): Flow<List<Transaction>> = flow {
        emit(recentTransactions)
    }

    fun getAcquirerRankings(): Flow<List<Acquirer>> = flow {
        emit(mockAcquirers)
    }
}
