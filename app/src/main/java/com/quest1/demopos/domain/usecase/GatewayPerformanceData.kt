package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.payment.GatewayPerformanceMetrics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the performance data for each gateway.
 *
 * It is marked as a @Singleton so that only ONE instance of this class
 * exists for the app's entire lifecycle. This ensures the performance data
 * is not lost between different payment attempts.
 */
@Singleton
class GatewayPerformanceData @Inject constructor() {
    /**
     * Stores a list of all recent performance metrics events. The MAB algorithm
     * will process this list in-memory to calculate success rates.
     */
    val metrics = mutableListOf<GatewayPerformanceMetrics>()
}