package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTerminalIdUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    fun execute(): Flow<String> {
        return coreRepository.getTerminalId()
    }
}