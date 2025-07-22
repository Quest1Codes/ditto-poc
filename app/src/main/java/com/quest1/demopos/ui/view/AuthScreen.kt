package com.quest1.demopos.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * A composable screen for user login and registration.
 *
 * @param onLoginSuccess A callback invoked with the user's role upon successful authentication.
 * @param viewModel The view model that handles the authentication logic.
 */
@Composable
fun AuthScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // State for UI input fields
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("cashier") } // Default role for registration
    var isRegistering by remember { mutableStateOf(false) }

    // Observe the authentication state from the ViewModel
    val authState by viewModel.authState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRegistering) "Register" else "Login",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Show role field only during registration
            if (isRegistering) {
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role (e.g., cashier, manager)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Handle different UI states
            when (val state = authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator()
                }
                is AuthState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is AuthState.Success -> {
                    // Trigger navigation upon successful login
                    LaunchedEffect(state.role) {
                        onLoginSuccess(state.role)
                    }
                }
                else -> {} // Idle state
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main action button
            Button(
                onClick = {
                    if (isRegistering) {
                        viewModel.register(username, password, role)
                    } else {
                        viewModel.login(username, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isRegistering) "Create Account" else "Login")
            }

            // Toggle between Login and Register modes
            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(if (isRegistering) "Already have an account? Login" else "Don't have an account? Register")
            }
        }
    }
}