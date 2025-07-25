package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.BuildConfig
import com.quest1.demopos.domain.usecase.order.ProcessPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PaymentStatus {
    INITIATING,
    PROCESSING,
    SUCCESSFUL,
    FAILED,
    REDIRECTING
}

data class PaymentUiState(
    val status: PaymentStatus = PaymentStatus.INITIATING,
    val progress: Float = 0f,
    val errorMessage: String? = null
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val processPaymentUseCase: ProcessPaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    // Channel for one-time navigation events
    private val _navigationEvent = Channel<Unit>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val payInitDelay = BuildConfig.PayInitiatingDelay
    private val payRedirectDelay = BuildConfig.PayRedirectingDelay


    fun startPaymentProcess() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = PaymentStatus.INITIATING) }
            delay(payInitDelay)

            _uiState.update { it.copy(status = PaymentStatus.PROCESSING) }
            val result = processPaymentUseCase.execute()

            result.onSuccess { response ->
                if (response.status == "SUCCESS") {
                    _uiState.update { it.copy(status = PaymentStatus.SUCCESSFUL) }
                } else {
                    _uiState.update {
                        it.copy(
                            status = PaymentStatus.FAILED,
                            errorMessage = response.failureReason ?: "Unknown error"
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        status = PaymentStatus.FAILED,
                        errorMessage = error.message ?: "An unexpected error occurred."
                    )
                }
            }
        }
    }

    fun startRedirectHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = PaymentStatus.REDIRECTING) }
            delay(payRedirectDelay) // Keep a short delay to show the redirecting message
            _navigationEvent.send(Unit) // Signal navigation
        }
    }

    fun reset() {
        _uiState.value = PaymentUiState()
    }
}