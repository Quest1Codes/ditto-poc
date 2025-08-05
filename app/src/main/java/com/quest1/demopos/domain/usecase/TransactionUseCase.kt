package com.quest1.demopos.domain.usecase

import androidx.compose.ui.graphics.Color
import com.quest1.demopos.R
import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.GatewayStatusInfo
import com.quest1.demopos.data.model.analytics.StorePerformance
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.repository.DittoRepository
import com.quest1.demopos.ui.theme.Error
import com.quest1.demopos.ui.theme.Success
import com.quest1.demopos.ui.theme.Warning
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class TransactionUseCase @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    fun getTransactions(): Flow<List<Transaction>> {
        return dittoRepository.observeTransactions()
    }

    fun getRecentTransactions(): Flow<List<Transaction>> {
        return getTransactions().map { it.take(5) }
    }

    fun getStorePerformance(): Flow<StorePerformance> {
        return getTransactions().map { transactions ->
            val totalTransactions = transactions.size
            val successfulTransactions = transactions.filter { it.status == "SUCCESS" }
            val revenueToday = successfulTransactions.sumOf { it.amount }

            StorePerformance(
                totalTransactions = totalTransactions,
                transactionGrowth = "",
                revenueToday = formatCurrency(revenueToday),
                revenueGrowth = ""
            )
        }
    }

    fun getAcquirerRankings(): Flow<List<Acquirer>> {
        return getTransactions().map { transactions ->
            if (transactions.isEmpty()) return@map emptyList()

            val statsByGateway = transactions.groupBy { it.acquirerName.lowercase(Locale.ROOT) }

            statsByGateway.map { (_, transactionList) ->
                val totalAttempts = transactionList.size
                val totalSuccesses = transactionList.count { it.status == "SUCCESS" }
                val successRate = if (totalAttempts > 0) {
                    (totalSuccesses.toDouble() / totalAttempts.toDouble()) * 100
                } else {
                    0.0
                }
                val avgLatency = transactionList.map { it.latencyMs }.average().toLong()

                Acquirer(
                    rank = 0,
                    name = transactionList.first().acquirerName,
                    statusInfo = getStatusInfoForLatency(avgLatency),
                    latency = if (avgLatency > 0) "${avgLatency}ms" else "N/A",
                    successRate = "%.0f".format(successRate) + "%"
                )
            }
                .sortedByDescending { it.successRate.removeSuffix("%").toDoubleOrNull() ?: 0.0 }
                .mapIndexed { index, acquirer ->
                    acquirer.copy(rank = index + 1)
                }
        }
    }

    private fun getStatusInfoForLatency(avgLatency: Long): GatewayStatusInfo {
        return when {
            avgLatency <= 0 -> GatewayStatusInfo(R.drawable.signal_cellular_nodata_24px, Color.Gray)
            avgLatency < 8000 -> GatewayStatusInfo(R.drawable.signal_cellular_alt_24px, Success)
            avgLatency < 10000 -> GatewayStatusInfo(R.drawable.signal_cellular_alt_2_bar_24px, Warning)
            else -> GatewayStatusInfo(R.drawable.signal_cellular_alt_1_bar_24px, Error)
        }
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("en", "US")).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }.format(amount)
    }
}