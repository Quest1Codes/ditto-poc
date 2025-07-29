package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.repository.PaymentRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Implements the Epsilon-Greedy MAB algorithm to select a payment gateway.
 */
@Singleton
class MabGatewaySelector @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val performanceData: GatewayPerformanceData
) {
    // Epsilon (ε) is the exploration rate. 40% of the time, we will EXPLORE.
    private val epsilon = 0.4

    suspend fun selectGateway(): Gateway {
        val availableGateways = paymentRepository.getAvailableGateways().first()
        if (availableGateways.isEmpty()) {
            throw IllegalStateException("No payment gateways available.")
        }

        // Decide whether to explore or exploit
        if (Random.nextDouble() < epsilon || performanceData.metrics.isEmpty()) {
            // --- EXPLORE ---
            return availableGateways.random()
        } else {
            // --- EXPLOIT ---
            return findBestGateway(availableGateways)
        }
    }

    /**
     * Finds the gateway with the highest success rate by processing the
     * list of stored performance metrics.
     */
    private fun findBestGateway(gateways: List<Gateway>): Gateway {
        // Group all historical metrics by their gateway ID
        val statsByGateway = performanceData.metrics.groupBy { it.gatewayId }

        val bestGateway = gateways.maxByOrNull { gateway ->
            val stats = statsByGateway[gateway.id]
            if (stats != null && stats.isNotEmpty()) {
                // Calculate success rate: (number of successful metrics) / (total number of metrics)
                stats.count { it.wasSuccess }.toDouble() / stats.size.toDouble()
            } else {
                -1.0 // This gateway has no history, rank it lowest
            }
        }

        return bestGateway ?: gateways.random()
    }
}