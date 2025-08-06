package com.quest1.demopos.ui.view

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.BuildConfig
import com.quest1.demopos.R
import com.quest1.demopos.domain.usecase.MabGatewaySelector
import com.quest1.demopos.domain.usecase.ProcessPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

enum class PaymentStatus {
    SELECTING_GATEWAY,
    INITIATING,
    PROCESSING,
    SUCCESSFUL,
    FAILED,
    REDIRECTING
}

data class PaymentUiState(
    val status: PaymentStatus = PaymentStatus.SELECTING_GATEWAY,
    val progress: Float = 0f,
    val errorMessage: String? = null,
    @DrawableRes val acquirerLogoRes: Int? = null
)

@DrawableRes
private fun getLogoForProcessor(processorName: String): Int {
    return when (processorName.lowercase(Locale.ROOT)) {
        "stripe" -> R.drawable.stripe_logo
        "paypal" -> R.drawable.paypal_logo
        "adyen" -> R.drawable.adyen_logo
        else -> R.drawable.credit_card_24px
    }
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val mabGatewaySelector: MabGatewaySelector
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()
    private val _navigationEvent = Channel<Unit>()
    val navigationEvent = _navigationEvent.receiveAsFlow()
    private val payInitDelay = BuildConfig.PayInitiatingDelay
    private val payRedirectDelay = BuildConfig.PayRedirectingDelay

    fun startPaymentProcess() {
        viewModelScope.launch {
            _uiState.update { it.copy(status = PaymentStatus.SELECTING_GATEWAY) }
            val selectedAcquirer = mabGatewaySelector.selectGateway()
            val logoRes = getLogoForProcessor(selectedAcquirer.name)

            delay(1500)
            _uiState.update {
                it.copy(
                    status = PaymentStatus.INITIATING,
                    acquirerLogoRes = logoRes
                )
            }
            delay(payInitDelay)

            _uiState.update { it.copy(status = PaymentStatus.PROCESSING) }
            val result = processPaymentUseCase.execute(selectedAcquirer)

            result.onSuccess { response ->
                if (response.status == "SUCCESS") {
                    _uiState.update {
                        it.copy(
                            status = PaymentStatus.SUCCESSFUL,
                            acquirerLogoRes = logoRes
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            status = PaymentStatus.FAILED,
                            errorMessage = response.failureReason ?: "Unknown error",
                            acquirerLogoRes = logoRes
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
            delay(payRedirectDelay)
            _navigationEvent.send(Unit)
        }
    }

    fun reset() {
        _uiState.value = PaymentUiState()
    }
}