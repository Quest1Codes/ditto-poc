package com.quest1.demopos.ui.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class PaymentGatewayUiState(
    val cardHolderName: String = "Jonathan Michael",
    val last4CardDigits: String = "3456",
    val company: String = "MyShop",
    val orderNumber: String = "",
    val productSummary: String = "Shopping Cart",
    val vatAmount: String = "$29.99",
    val totalAmount: String = "0.00",
    val totalAmountMajor: String = "150",
    val totalAmountMinor: String = "97",
    val cardNumber: String = "2412 •••• •••• ••••",
    val cvv: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val password: String = "",
    val isCardNumberEditable: Boolean = false
)

@HiltViewModel
class PaymentGatewayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = mutableStateOf(PaymentGatewayUiState())
    val uiState: State<PaymentGatewayUiState> = _uiState

    init {
        val totalAmount = savedStateHandle.get<String>("totalAmount")?.toDoubleOrNull() ?: 0.0
        val orderNumber = savedStateHandle.get<String>("orderNumber") ?: "N/A"
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))
        val formattedTotal = currencyFormat.format(totalAmount).removePrefix("$")

        _uiState.value = _uiState.value.copy(
            totalAmount = formattedTotal,
            totalAmountMajor = formattedTotal.substringBefore('.'),
            totalAmountMinor = formattedTotal.substringAfter('.', "00"),
            orderNumber = orderNumber
        )
    }

    fun onCardNumberChanged(newValue: String) {
        if (newValue.length <= 16 && newValue.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(cardNumber = newValue)
        }
    }

    fun onCvvChanged(newValue: String) {
        if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(cvv = newValue)
        }
    }

    fun onExpiryMonthChanged(newValue: String) {
        if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(expiryMonth = newValue)
        }
    }

    fun onExpiryYearChanged(newValue: String) {
        if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(expiryYear = newValue)
        }
    }

    fun onPasswordChanged(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    fun onEditCardNumber(isEditable: Boolean) {
        val currentCardNumber = if(isEditable) "" else "2412 •••• •••• ••••"
        _uiState.value = _uiState.value.copy(
            isCardNumberEditable = isEditable,
            cardNumber = currentCardNumber
        )
    }
}