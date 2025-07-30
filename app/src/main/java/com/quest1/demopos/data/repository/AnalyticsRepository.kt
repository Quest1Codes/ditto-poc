package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.StorePerformance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor(
    private val gatewayPerformanceRepository: GatewayPerformanceRepositoryImpl // Inject the new repo
) {

    fun getAcquirerRankings(): Flow<List<Acquirer>> {
        // Fetch live, sorted data and map it to the UI model
        return gatewayPerformanceRepository.observePerformanceRankings().map { performanceList ->
            performanceList.mapIndexed { index, performance ->
                val status = when {
                    performance.successRate >= 0.80 -> "healthy"
                    performance.successRate >= 0.50 -> "degraded"
                    else -> "failing"
                }
                Acquirer(
                    rank = index + 1,
                    name = performance.gatewayName,
                    status = status,
                    latency = "N/A", // You can enhance GatewayPerformance to track this
                    successRate = "%.2f".format(performance.successRate * 100) + "%"
                )
            }
        }
    }
}