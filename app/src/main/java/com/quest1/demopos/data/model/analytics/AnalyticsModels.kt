package com.quest1.demopos.data.model.analytics

data class Acquirer(
    val rank: Int,
    val name: String,
    val status: String, // "healthy", "degraded", "failing"
    val latency: String,
    val successRate: String
)

data class Transaction(
    val id: String,
    val amount: String,
    val time: String,
    val status: String // "success", "failed"
)

data class StorePerformance(
    val totalTransactions: Int,
    val transactionGrowth: String,
    val revenueToday: String,
    val revenueGrowth: String
)
