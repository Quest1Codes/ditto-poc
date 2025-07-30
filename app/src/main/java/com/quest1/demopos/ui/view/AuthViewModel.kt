package com.quest1.demopos.ui.view

import android.content.SharedPreferences
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
import com.quest1.demopos.data.repository.SessionManager
import com.quest1.demopos.domain.usecase.UpsertTerminalUseCase
import androidx.core.content.edit
import com.auth0.jwt.JWT
import java.util.Base64
import java.util.Date

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dittoManager: DittoManager,
    private val upsertTerminalUseCase: UpsertTerminalUseCase,
    private val sessionManager: SessionManager,
    private val encryptedPrefs: SharedPreferences,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    private val _loginResultToken = MutableStateFlow<String?>(null)

    init {
        // On app start, check for valid token
        val token = encryptedPrefs.getString("auth_token", null)
        val role = encryptedPrefs.getString("auth_role", "")
        val username = encryptedPrefs.getString("auth_username", "")

        if (token != null && isTokenValid(token)) {
            _loginResultToken.value = token
            _authState.value = AuthState.Success(role ?: "")
            sessionManager.userLoggedIn(username ?: "")
            viewModelScope.launch {
                upsertTerminalUseCase.execute(username ?: "") // Using username as the terminal ID
            }
        } else {
            logout()
        }

        // Observe both when Ditto needs a token and when we have one
        viewModelScope.launch {
            combine(
                dittoManager.isAuthenticationRequired,
                _loginResultToken
            ) { isRequired, token ->
                Log.d("AuthViewModel", "combine: isRequired=$isRequired, token=${token != null}")
                if (isRequired && token != null) {
                    Log.d("AuthViewModel", "Ditto requires auth and we have a token=$token. Providing it now.")
                    dittoManager.provideTokenToAuthenticator(token)
                }
            }.collect()
        }
    }

    fun isTokenValid(jwtToken: String): Boolean {
        try {
            val claims = JWT.decode(jwtToken) // Does not verify signature
            return claims.expiresAt.after(Date()) ?: false
        } catch (e: Exception) {
            e.message?.let { Log.e("AuthViewModel", it) }
            return false
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(username, password)

            result.onSuccess { loginResult ->

                encryptedPrefs.edit {
                    putString("auth_token", loginResult.accessToken)
                    putString("auth_role", loginResult.role)
                    putString("auth_username", username)
                }

                // Store the token. The observer in init {} will handle giving it to Ditto.
                sessionManager.userLoggedIn(username)
                _loginResultToken.value = loginResult.accessToken
                _authState.value = AuthState.Success(loginResult.role)
                viewModelScope.launch {
                    upsertTerminalUseCase.execute(username) // Using username as the terminal ID
                }
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

    fun logout () {
        dittoManager.logout()
        encryptedPrefs.edit {
            remove("auth_token")
            remove("auth_role")
            remove("auth_username")
        }
    }
}