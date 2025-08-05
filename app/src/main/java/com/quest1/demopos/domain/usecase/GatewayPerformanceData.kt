package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.payment.GatewayPerformanceMetrics
import javax.inject.Inject
import javax.inject.Singleton

// Holds the performance data for each gateway.

@Singleton
class GatewayPerformanceData @Inject constructor() {
    val metrics = mutableListOf<GatewayPerformanceMetrics>()
}