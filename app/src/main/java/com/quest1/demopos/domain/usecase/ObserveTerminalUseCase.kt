package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.core.TerminalInfo
import com.quest1.demopos.data.repository.CoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTerminalInfoUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    fun execute(): Flow<TerminalInfo> {
        return coreRepository.observeTerminalInfo()
    }
}