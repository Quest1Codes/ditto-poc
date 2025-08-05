package com.quest1.demopos.data.repository

import androidx.compose.ui.graphics.Color
import com.quest1.demopos.R
import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.GatewayStatusInfo
import com.quest1.demopos.domain.usecase.GatewayPerformanceData
import com.quest1.demopos.ui.theme.Error
import com.quest1.demopos.ui.theme.Success
import com.quest1.demopos.ui.theme.Warning
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepository @Inject constructor(
    private val gatewayPerformanceRepository: GatewayPerformanceRepositoryImpl,
    private val performanceData: GatewayPerformanceData
) {
    fun getAcquirerRankings(): Flow<List<Acquirer>> {
        return gatewayPerformanceRepository.observePerformanceRankings().map { performanceList ->
            val allMetrics = performanceData.metrics

            performanceList.mapIndexed { index, performance ->
                val gatewayMetrics = allMetrics.filter { it.gatewayId == performance.gatewayId }
                val avgLatency = if (gatewayMetrics.isNotEmpty()) {
                    val totalLatency = gatewayMetrics.sumOf { (it.metrics["latencyMs"] as? Long) ?: 0L }
                    (totalLatency / gatewayMetrics.size)
                } else {
                    0L
                }

                // Determine the status icon and color based on latency
                val statusInfo = when {
                    avgLatency <= 0 -> GatewayStatusInfo(R.drawable.signal_cellular_nodata_24px, Color.Gray)
                    avgLatency < 8000 -> GatewayStatusInfo(R.drawable.signal_cellular_alt_24px, Success)
                    avgLatency < 10000 -> GatewayStatusInfo(R.drawable.signal_cellular_alt_2_bar_24px, Warning)
                    else -> GatewayStatusInfo(R.drawable.signal_cellular_alt_1_bar_24px, Error)
                }

                val latencyString = if (avgLatency > 0) "${avgLatency}ms" else "N/A"

                Acquirer(
                    rank = index + 1,
                    name = performance.gatewayName,
                    statusInfo = statusInfo,
                    latency = latencyString,
                    successRate = "%.0f".format(performance.successRate) + "%"
                )
            }
        }
    }
}