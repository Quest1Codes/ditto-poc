package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.repository.DittoRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetOptimalGatewayUseCase @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    suspend fun execute(): String {
        val bestPerformer = dittoRepository.observePerformanceRankings().firstOrNull()?.firstOrNull()
        return bestPerformer?.gatewayName ?: "stripe"
    }
}