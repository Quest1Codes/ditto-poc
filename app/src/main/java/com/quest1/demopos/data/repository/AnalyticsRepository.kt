package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.StorePerformance
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



    fun getAcquirerRankings(): Flow<List<Acquirer>> = flow {
        emit(mockAcquirers)
    }
}
