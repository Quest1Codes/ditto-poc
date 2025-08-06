package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.core.Terminal
import com.quest1.demopos.data.repository.DittoRepository
import javax.inject.Inject

class UpsertTerminalUseCase @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    suspend fun execute(terminalId: String) {
        val terminal = Terminal(
            _id = terminalId,
            storeId = "store_01",
            name = "Terminal - $terminalId",
            ipAddress = "192.168.1.1",
            lastSeen = System.currentTimeMillis()
        )
        dittoRepository.upsertTerminal(terminal)
    }
}