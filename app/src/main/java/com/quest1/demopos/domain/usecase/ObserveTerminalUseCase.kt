package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.core.TerminalInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import live.ditto.ditto_wrapper.DittoManager
import javax.inject.Inject

class ObserveTerminalInfoUseCase @Inject constructor(
    private val dittoManager: DittoManager
) {
    fun execute(): Flow<TerminalInfo> = callbackFlow {
        val ditto = dittoManager.requireDitto()

        val observer = ditto.presence.observe { presenceGraph ->
            val peerKey = presenceGraph.localPeer.peerKeyString
            val isConnected = presenceGraph.remotePeers.isNotEmpty()
            val terminalInfo = TerminalInfo(peerKey = peerKey, isConnected = isConnected)
            trySend(terminalInfo)
        }

        awaitClose {
            observer.close()
        }
    }
}