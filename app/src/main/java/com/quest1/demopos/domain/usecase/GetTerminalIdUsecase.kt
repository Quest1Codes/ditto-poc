package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.repository.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTerminalIdUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    fun execute(): Flow<String> {
        return sessionManager.currentUserId.map { userId ->
            userId ?: "Offline"
        }
    }
}