package com.quest1.demopos.data.model.analytics

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

/**
 * Represents the visual state of a gateway's performance.
 * @param iconRes The drawable resource ID for the signal icon.
 * @param color The color to tint the icon.
 */
data class GatewayStatusInfo(
    @DrawableRes val iconRes: Int,
    val color: Color
)

data class Acquirer(
    val rank: Int,
    val name: String,
    val statusInfo: GatewayStatusInfo, // MODIFIED: Replaced status string
    val latency: String,
    val successRate: String
)

data class StorePerformance (
    val totalTransactions: Int,
    val transactionGrowth: String,
    val revenueToday: String,
    val revenueGrowth: String
)