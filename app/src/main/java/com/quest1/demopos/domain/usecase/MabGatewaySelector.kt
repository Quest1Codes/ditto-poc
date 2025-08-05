package com.quest1.demopos.domain.usecase

import android.util.Log
import com.quest1.demopos.data.model.payment.Gateway
import com.quest1.demopos.data.repository.PaymentRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MabGatewaySelector @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val transactionUseCase: TransactionUseCase
) {
    private val epsilon = 0.5
    private val successRateWeight = 0.7
    private val latencyWeight = 0.3

    suspend fun selectGateway(): Gateway {
        val availableGateways = paymentRepository.getAvailableGateways().first()
        if (availableGateways.isEmpty()) {
            throw IllegalStateException("No payment gateways available.")
        }

        val allTransactions = transactionUseCase.getTransactions().first()
        val triedGatewayIds = allTransactions.map { it.acquirerId }.toSet()
        val untriedGateways = availableGateways.filter { it.id !in triedGatewayIds }

        if (untriedGateways.isNotEmpty()) {
            val gatewayToSample = untriedGateways.first()
            Log.d("MabGatewaySelector", "INITIAL SAMPLING: Trying gateway '${gatewayToSample.name}' for the first time.")
            return gatewayToSample
        }

        if (Random.nextDouble() < epsilon) {
            // Explore: Randomly select a gateway.
            Log.d("MabGatewaySelector", "EXPLORING: Randomly selecting from available gateways.")
            return availableGateways.random()
        } else {
            // Exploit: Choose the best-performing gateway.
            Log.d("MabGatewaySelector", "EXPLOITING: Finding best performing gateway.")
            return findBestGateway(availableGateways)
        }
    }

    private suspend fun findBestGateway(gateways: List<Gateway>): Gateway {
        val allTransactions = transactionUseCase.getTransactions().first()
        val statsByGateway = allTransactions.groupBy { it.acquirerId }

        val allLatencies = allTransactions.map { it.latencyMs }
        val minLatency = allLatencies.minOrNull()?.toDouble() ?: 0.0
        val maxLatency = allLatencies.maxOrNull()?.toDouble() ?: 1.0

        val gatewayScores = gateways.associateWith { gateway ->
            val stats = statsByGateway[gateway.id]
            if (stats.isNullOrEmpty()) {
                -1.0
            } else {
                val successRate = stats.count { it.status == "SUCCESS" }.toDouble() / stats.size
                val avgLatency = stats.map { it.latencyMs }.average()
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