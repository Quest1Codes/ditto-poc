package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.repository.AuthRepository
import com.quest1.demopos.data.repository.LoginResult // Import the new data class
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import live.ditto.ditto_wrapper.DittoManager // Import DittoManager
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dittoManager: DittoManager // Inject DittoManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(username, password)
            result.onSuccess { loginResult ->
                // On successful login to your service, now login to Ditto
                dittoManager.loginWithToken(loginResult.accessToken)
                _authState.value = AuthState.Success(loginResult.role)
            }.onFailure { error ->
                _authState.value = AuthState.Error(
                    error.message ?: "An unknown error occurred"
                )
            }
        }
    }

    fun register(username: String, password: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.register(username, password, role)
            result.onSuccess {
                // Automatically log in after a successful registration
                login(username, password)
            }.onFailure { error ->
                _authState.value = AuthState.Error(
                    error.message ?: "Registration failed"
                )
            }
        }
    }
}