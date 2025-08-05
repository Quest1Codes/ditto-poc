// File: app/src/main/java/com/quest1/demopos/domain/usecase/MabGatewaySelector.kt
package com.quest1.demopos.domain.usecase

import android.util.Log
import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.repository.PaymentRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Implements an Epsilon-Greedy MAB algorithm to select a payment gateway,
 * with an initial sampling phase to ensure all gateways are tried at least once.
 */
@Singleton
class MabGatewaySelector @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val performanceData: GatewayPerformanceData
) {
    // Epsilon (ε) is the exploration rate. 50% of the time, we will EXPLORE.
    private val epsilon = 0.5
    private val successRateWeight = 0.7
    private val latencyWeight = 0.3

    suspend fun selectGateway(): Gateway {
        val availableGateways = paymentRepository.getAvailableGateways().first()
        if (availableGateways.isEmpty()) {
            throw IllegalStateException("No payment gateways available.")
        }

        // --- NEW: INITIAL SAMPLING PHASE ---
        // First, check if there are any gateways that have never been tried.
        val triedGatewayIds = performanceData.metrics.map { it.gatewayId }.toSet()
        val untriedGateways = availableGateways.filter { it.id !in triedGatewayIds }

        // If there are untried gateways, select the first one to ensure it gets sampled.
        if (untriedGateways.isNotEmpty()) {
            val gatewayToSample = untriedGateways.first()
            Log.d("MabGatewaySelector", "INITIAL SAMPLING: Trying gateway '${gatewayToSample.name}' for the first time.")
            return gatewayToSample
        }

        // --- REGULAR MAB LOGIC: EXPLORE/EXPLOIT PHASE ---
        // This runs only after all gateways have been sampled at least once.
        if (Random.nextDouble() < epsilon) {
            // --- EXPLORE ---
            Log.d("MabGatewaySelector", "EXPLORING: Randomly selecting from available gateways.")
            return availableGateways.random()
        } else {
            // --- EXPLOIT ---
            Log.d("MabGatewaySelector", "EXPLOITING: Finding best performing gateway.")
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

                Log.d("MabGatewaySelector", "Gateway Score: ${gateway.name} | Success Rate: ${"%.2f".format(successRate)} | Avg Latency: ${"%.0f".format(avgLatency)}ms | Final Score: ${"%.4f".format(score)}")

                score
            }
        }

        return gatewayScores.maxByOrNull { it.value }?.key ?: gateways.random()
    }
}