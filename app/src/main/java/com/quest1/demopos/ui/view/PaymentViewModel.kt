package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class PaymentStatus {
    INITIATING,
    PROCESSING,
    SUCCESSFUL,
    FAILED,
    REDIRECTING
}

data class PaymentUiState(
    val status: PaymentStatus = PaymentStatus.INITIATING,
    val progress: Float = 0f
)

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun startPaymentProcess() {
        viewModelScope.launch {
            // Stage 1: Initiating
            _uiState.update { it.copy(status = PaymentStatus.INITIATING) }
            delay(2000) // Simulate redirection time

            // Stage 2: Processing
            _uiState.update { it.copy(status = PaymentStatus.PROCESSING) }
            // Simulate processing progress
            for (i in 1..10) {
                delay(300)
                _uiState.update { it.copy(progress = i / 10f) }
            }

            // Stage 3: Success or Failure (50/50 chance for demo)
            val isSuccess = Random.nextBoolean()
            if (isSuccess) {
                _uiState.update { it.copy(status = PaymentStatus.SUCCESSFUL) }
            } else {
                _uiState.update { it.copy(status = PaymentStatus.FAILED) }
            }
        }
    }

    fun startRedirectHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = PaymentStatus.REDIRECTING) }
            delay(3000) // Simulate redirect delay
            // In a real app, you would navigate home here.
        }
    }

    fun reset() {
        _uiState.value = PaymentUiState()
    }
}