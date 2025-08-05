package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.repository.GatewayPerformanceRepositoryImpl
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject


class GetOptimalGatewayUseCase @Inject constructor(
    private val gatewayPerformanceRepository: GatewayPerformanceRepositoryImpl
) {
    suspend fun execute(): String {
        val bestPerformer = gatewayPerformanceRepository.observePerformanceRankings().firstOrNull()?.firstOrNull()
        return bestPerformer?.gatewayName ?: "stripe"
    }
}