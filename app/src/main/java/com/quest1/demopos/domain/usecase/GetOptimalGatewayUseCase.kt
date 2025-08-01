package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.repository.GatewayPerformanceRepositoryImpl
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * A use case to determine the best-performing payment gateway.
 * It fetches the list of gateways sorted by their success rate from the repository
 * and returns the name of the top-ranked gateway.
 */
class GetOptimalGatewayUseCase @Inject constructor(
    private val gatewayPerformanceRepository: GatewayPerformanceRepositoryImpl
) {
    suspend fun execute(): String {
        // Fetch the list of gateways, which is already sorted by successRate descending.
        val bestPerformer = gatewayPerformanceRepository.observePerformanceRankings().firstOrNull()?.firstOrNull()

        // Return the name of the best gateway, or fallback to a default if none are available.
        return bestPerformer?.gatewayName ?: "stripe"
    }
}