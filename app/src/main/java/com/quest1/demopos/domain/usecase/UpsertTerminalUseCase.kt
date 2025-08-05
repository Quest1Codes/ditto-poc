package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.core.Terminal
import com.quest1.demopos.data.repository.CoreRepository
import javax.inject.Inject

class UpsertTerminalUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend fun execute(userId: String) {
        val terminal = Terminal(
            _id = userId,
            storeId = "store_01",
            name = "Terminal-$userId",
            ipAddress = "192.168.1.101",
            lastSeen = System.currentTimeMillis()
        )
        coreRepository.upsertTerminal(terminal)
    }
}