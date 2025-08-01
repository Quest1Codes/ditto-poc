package com.quest1.demopos.domain.usecase

import android.util.Log
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
    private val successRateWeight = 0.7
    private val latencyWeight = 0.3

    suspend fun selectGateway(): Gateway {
        val availableGateways = paymentRepository.getAvailableGateways().first()
        if (availableGateways.isEmpty()) {
            throw IllegalStateException("No payment gateways available.")
        }

        // Decide whether to explore or exploit
        if (Random.nextDouble() < epsilon || performanceData.metrics.isEmpty()) {
            // --- EXPLORE ---
            Log.d("MabGatewaySelector", "Exploring available gateways.")
            return availableGateways.random()
        } else {
            // --- EXPLOIT ---
            Log.d("MabGatewaySelector", "Exploiting best available gateway.")
            return findBestGateway(availableGateways)
        }
    }

    /**
     * Finds the gateway with the highest score based on a weighted average
     * of success rate and latency.
     */
    private fun findBestGateway(gateways: List<Gateway>): Gateway {
        val statsByGateway = performanceData.metrics.groupBy { it.gatewayId }

        val allLatencies = performanceData.metrics
            .mapNotNull { it.metrics["latencyMs"] as? Long }
        val minLatency = allLatencies.minOrNull()?.toDouble() ?: 0.0
        val maxLatency = allLatencies.maxOrNull()?.toDouble() ?: 1.0

        val gatewayScores = gateways.associateWith { gateway ->
            val stats = statsByGateway[gateway.id]
            if (stats.isNullOrEmpty()) {
                -1.0
            } else {
                val successRate = stats.count { it.wasSuccess }.toDouble() / stats.size
                val avgLatency = stats.mapNotNull { it.metrics["latencyMs"] as? Long }
                    .let { latencies -> if (latencies.isEmpty()) 0.0 else latencies.average() }
                val normalizedLatency = if ((maxLatency - minLatency) > 0) {
                    1 - ((avgLatency - minLatency) / (maxLatency - minLatency))
                } else {
                    1.0
                }
                val score = (successRateWeight * successRate) + (latencyWeight * normalizedLatency)

                // **ADDED**: Log the detailed score for each gateway
                Log.d("MabGatewaySelector", "Gateway Score: ${gateway.name} | Success Rate: ${"%.2f".format(successRate)} | Avg Latency: ${"%.0f".format(avgLatency)}ms | Final Score: ${"%.4f".format(score)}")

                score
            }
        }

        return gatewayScores.maxByOrNull { it.value }?.key ?: gateways.random()
    }
}