package com.quest1.demopos.data.model.analytics

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

// Represents the visual state of a gateway's performance.
data class GatewayStatusInfo(
    @DrawableRes val iconRes: Int,
    val color: Color
)

data class Acquirer(
    val rank: Int,
    val name: String,
    val statusInfo: GatewayStatusInfo,
    val latency: String,
    val successRate: String
)

data class StorePerformance (
    val totalTransactions: Int,
    val transactionGrowth: String,
    val revenueToday: String,
    val revenueGrowth: String
)