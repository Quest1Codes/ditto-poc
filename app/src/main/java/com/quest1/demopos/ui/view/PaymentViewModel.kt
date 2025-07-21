package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.domain.usecase.order.ProcessPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val processPaymentUseCase: ProcessPaymentUseCase // Inject the use case
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun startPaymentProcess() {
        viewModelScope.launch {
            // Stage 1: Initiating
            _uiState.update { it.copy(status = PaymentStatus.INITIATING) }
            delay(1500) // Simulate initial setup time

            // Stage 2: Processing
            _uiState.update { it.copy(status = PaymentStatus.PROCESSING) }

            // Execute the payment logic via the use case
            val result = processPaymentUseCase.execute()

            // Stage 3: Success or Failure
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
            delay(3000) // Simulate redirect delay
            // In a real app, navigation would be triggered here.
        }
    }

    fun reset() {
        _uiState.value = PaymentUiState()
    }
}