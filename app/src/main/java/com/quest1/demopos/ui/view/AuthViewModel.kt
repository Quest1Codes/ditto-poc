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
import kotlinx.coroutines.flow.collect // <-- ADDED IMPORT
import kotlinx.coroutines.flow.combine
import android.util.Log
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dittoManager: DittoManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    private val _loginResultToken = MutableStateFlow<String?>(null)

    init {
        // Observe both when Ditto needs a token and when we have one
        viewModelScope.launch {
            combine(
                dittoManager.isAuthenticationRequired,
                _loginResultToken
            ) { isRequired, token ->
                Log.d("AuthViewModel", "combine: isRequired=$isRequired, token=${token != null}")
                if (isRequired && token != null) {
                    Log.d("AuthViewModel", "Ditto requires auth and we have a token. Providing it now.")
                    dittoManager.provideTokenToAuthenticator(token)
                }
            }.collect()
        }
    }
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(username, password)

            result.onSuccess { loginResult ->
                // Store the token. The observer in init {} will handle giving it to Ditto.
                _loginResultToken.value = loginResult.accessToken
                _authState.value = AuthState.Success(loginResult.role)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "An unknown error occurred")
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