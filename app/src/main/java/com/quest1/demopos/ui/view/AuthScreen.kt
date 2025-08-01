package com.quest1.demopos.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quest1.demopos.R // Make sure to import your R file
import com.quest1.demopos.ui.theme.LightTextPrimary
import androidx.compose.foundation.Image
@Composable
fun AuthScreen(
    onLoginSuccess: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var isRegistering by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Toggles between Login and Register UIs
        if (isRegistering) {
            RegisterUI(viewModel = viewModel) {
                isRegistering = false
            }
        } else {
            LoginUI(viewModel = viewModel, onLoginSuccess = onLoginSuccess) {
                isRegistering = true
            }
        }
    }
}

@Composable
fun LoginUI(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // State for the User ID and Password fields
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Quest1 POS",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = LightTextPrimary
        )
        Text(
            text = "Welcome back! Login into your account!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // User ID input field
        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.mail_24px),
                    contentDescription = "User ID Icon"
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // **FIXED**: Corrected `onValueValueChange` to `onValueChange`
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "Password Icon"
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Forgot Password button
        TextButton(
            onClick = { /* TODO: Handle forgot password */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(userId, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation to Register screen
        TextButton(onClick = onNavigateToRegister) {
            Text("Don't have an account yet? Register here.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray)
        }


        // Handles UI updates for different authentication states
        when (val state = authState) {
            is AuthState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            is AuthState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
            is AuthState.Success -> {
                LaunchedEffect(state.role) {
                    onLoginSuccess(state.role)
                }
            }
            else -> {}
        }
    }
}

@Composable
fun RegisterUI(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Terminal") }
    var expanded by remember { mutableStateOf(false) } // State to control dropdown
    val roles = listOf("Terminal", "Admin")
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Icon
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Account Now",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = LightTextPrimary
        )
        Text(
            text = "Create you POS Account Now!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        // User ID input field
        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.mail_24px),
                    contentDescription = "User ID Icon"
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "Password Icon"
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role dropdown
        Box {
            OutlinedTextField(
                value = role,
                onValueChange = {},
                label = { Text("Role") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(12.dp),
                readOnly = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.person_24px),
                        contentDescription = "Role Icon"
                    )
                },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, "Dropdown arrow", Modifier.clickable { expanded = true })
                }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(350.dp)
            ) {
                roles.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray) },
                        onClick = {
                            role = selection
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register(userId, password, role.lowercase()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation to Login screen
        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray)
        }

        // Handles UI updates for different registration states
        when (val state = authState) {
            is AuthState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            is AuthState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
            is AuthState.Success -> {
                // Navigate back to login on successful registration
                LaunchedEffect(Unit) {
                    onNavigateToLogin()
                }
            }
            else -> {}
        }
    }
}