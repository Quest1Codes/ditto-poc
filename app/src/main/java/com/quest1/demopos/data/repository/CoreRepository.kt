package com.quest1.demopos.data.repository

import android.util.Log
import com.quest1.demopos.data.model.core.Terminal
import com.quest1.demopos.data.model.core.TerminalInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import live.ditto.ditto_wrapper.DittoManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreRepository @Inject constructor(
    private val dittoManager: DittoManager,
    private val sessionManager: SessionManager
) {

    suspend fun saveTerminal(terminal: Terminal) {
        try {
            val ditto = dittoManager.requireDitto()
            val queryString = "INSERT INTO ${Terminal.COLLECTION_NAME} DOCUMENTS (:terminal) ON ID CONFLICT DO UPDATE"
            val args = mapOf("terminal" to terminal.toDocument())
            ditto.store.execute(queryString, args)
        } catch (e: Exception) {
            Log.d("CoreRepository","Error upserting terminal: ${e.message}")
        }
    }

    fun getTerminalId(): Flow<String> {
        return sessionManager.currentUserId.map { userId ->
            userId ?: "Offline"
        }
    }

    fun observeTerminalInfo(): Flow<TerminalInfo> {
        val ditto = dittoManager.requireDitto()
        val peerKey = ditto.presence.graph.localPeer.peerKeyString
        val info = TerminalInfo(
            peerKey = peerKey,
            isConnected = !peerKey.isNullOrBlank()
        )
        return flowOf(info)
    }
}