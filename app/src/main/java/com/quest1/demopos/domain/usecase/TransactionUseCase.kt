package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.analytics.StorePerformance
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

class TransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    fun getTransactions(): Flow<List<Transaction>> {
        return transactionRepository.observeTransactions();
    }

    fun getRecentTransactions(): Flow<List<Transaction>> {
        return getTransactions().map{
                transactions -> transactions.take(5)
        };
    }


    // --- Flow of StorePerformance aggregation ---
    fun getStorePerformance(): Flow<StorePerformance> {
        val todayUnixTime = getStartOfDayUnixTimestamp();
        return getTransactions()
            .map { transactions ->
                val todayTransactions = transactions.filter { it.createdAt > todayUnixTime }
                val todayRevenue = todayTransactions.filter { it.status == "SUCCESS" }.sumOf { it.amount }
                val totalRevenue = transactions.filter { it.status == "SUCCESS" }.sumOf { it.amount }

                var transactionGrowth =100
                var revenueGrowth = 100

                if(transactions.size > todayTransactions.size) {
                    transactionGrowth = (todayTransactions.size /(transactions.size - todayTransactions.size)) * 100
                }
                if(totalRevenue > totalRevenue) {
                    revenueGrowth = ((totalRevenue / (totalRevenue - todayRevenue)) * 100).toInt()
                }


                StorePerformance(
                    totalTransactions = transactions.size,
                    transactionGrowth = "+$transactionGrowth% from yesterday",  // TODO: implement real growth calc
                    revenueToday = formatCurrency(
                        todayTransactions.sumOf { it.amount },
                        todayTransactions.firstOrNull()?.currency ?: "USD"
                    ),
                    revenueGrowth = "+$revenueGrowth% from yesterday"        // TODO: implement real growth calc
                )
            }
    }

    private fun formatCurrency(amount: Double, currency: String): String {
        val format = NumberFormat.getCurrencyInstance(Locale.US) // or use currency code
        format.currency = java.util.Currency.getInstance(currency)
        return format.format(amount)
    }

    fun getStartOfDayUnixTimestamp(): Long {
        return LocalDate.now()
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toEpochSecond()
    }
}
