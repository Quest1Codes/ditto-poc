package com.quest1.demopos.ui.view


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.core.TerminalInfo
import com.quest1.demopos.domain.usecase.GetTerminalIdUseCase
import com.quest1.demopos.domain.usecase.ObserveTerminalInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShopTopBarViewModel @Inject constructor(
    getTerminalIdUseCase: GetTerminalIdUseCase,
    observeTerminalInfoUseCase: ObserveTerminalInfoUseCase
) : ViewModel() {
    val terminalId: StateFlow<String> = getTerminalIdUseCase.execute()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Loading..."
        )
    val terminalInfo: StateFlow<TerminalInfo> = observeTerminalInfoUseCase.execute()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TerminalInfo(peerKey = null, isConnected = false)
        )
}