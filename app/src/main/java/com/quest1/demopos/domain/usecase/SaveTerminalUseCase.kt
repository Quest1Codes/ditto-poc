package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.core.Terminal
import com.quest1.demopos.data.repository.CoreRepository
import javax.inject.Inject

class SaveTerminalUseCase @Inject constructor(
    private val coreRepository: CoreRepository
) {
    suspend fun execute(userId: String) {
        val terminal = Terminal(
            _id = userId,
            storeId = "store_01", // Common stubbed Store ID
            name = "Terminal-$userId",
            ipAddress = "192.168.1.101", // Stubbed IP Address
            lastSeen = System.currentTimeMillis()
        )
        coreRepository.saveTerminal(terminal)
    }
}