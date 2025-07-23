package com.quest1.demopos.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quest1.demopos.R
import com.quest1.demopos.ui.components.PrimaryActionButton
import com.quest1.demopos.ui.theme.LightSurface
import com.quest1.demopos.ui.theme.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewayScreen(
    onNavigateBack: () -> Unit,
    onPayNowClicked: () -> Unit,
    viewModel: PaymentGatewayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AceCoin Pay", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = { TimerChip() }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                SecurityMessage()
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryActionButton(
                    text = "Pay Now",
                    onClick = onPayNowClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            OrderSummaryCard(uiState)
            Spacer(modifier = Modifier.height(24.dp))
            PaymentDetailsForm(viewModel)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun TimerChip() {
    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.Black)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.timer_24px),
            contentDescription = "Timer",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("03:19", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OrderSummaryCard(uiState: PaymentGatewayUiState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Holder Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(uiState.cardHolderName, fontWeight = FontWeight.SemiBold)
                    Text("•••• ${uiState.last4CardDigits}")
                }
                Text("09/22", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.mastercard_logo),
                    contentDescription = "Mastercard Logo",
                    modifier = Modifier.height(24.dp)
                )
            }
            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Order Details
            SummaryRow("Company", uiState.company)
            SummaryRow("Order Number", uiState.orderNumber)
            SummaryRow("Product", uiState.productSummary)
            SummaryRow("VAT (20%)", uiState.vatAmount)

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("You have to Pay", style = MaterialTheme.typography.bodyLarge)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = uiState.totalAmountMajor,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                    Text(
                        text = ".${uiState.totalAmountMinor}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "USD",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PaymentDetailsForm(viewModel: PaymentGatewayViewModel) {
    val uiState by viewModel.uiState

    Column {
        // Card Number
        Text("Card Number", fontWeight = FontWeight.SemiBold)
        Text("Enter the 16-digit card number on the card", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.cardNumber,
            onValueChange = viewModel::onCardNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            readOnly = !uiState.isCardNumberEditable,
            leadingIcon = { Icon(painterResource(id = R.drawable.credit_card_24px), "Card") },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Edit", style = MaterialTheme.typography.bodySmall)
                    Switch(
                        checked = uiState.isCardNumberEditable,
                        onCheckedChange = { viewModel.onEditCardNumber(it) }
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightSurface,
                unfocusedContainerColor = LightSurface,
                disabledContainerColor = LightSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // CVV
            Column(modifier = Modifier.weight(1f)) {
                Text("CVV Number", fontWeight = FontWeight.SemiBold)
                Text("Enter the 3 or 4 digit number", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.cvv,
                    onValueChange = viewModel::onCvvChanged,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(painterResource(id = R.drawable.pin_24px), "CVV") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightSurface,
                        unfocusedContainerColor = LightSurface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = { Text("327", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = Color.LightGray)}
                )
            }

            // Expiry Date
            Column(modifier = Modifier.weight(1f)) {
                Text("Expiry Date", fontWeight = FontWeight.SemiBold)
                Text("Enter the expiration date", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = uiState.expiryMonth,
                        onValueChange = viewModel::onExpiryMonthChanged,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        placeholder = { Text("09", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = Color.LightGray)}
                    )
                    Text("/", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 24.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = uiState.expiryYear,
                        onValueChange = viewModel::onExpiryYearChanged,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightSurface,
                            unfocusedContainerColor = LightSurface,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        placeholder = { Text("22", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = Color.LightGray)}
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        Text("Password", fontWeight = FontWeight.SemiBold)
        Text("Enter your Dynamic password", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(painterResource(id = R.drawable.key_24px), "Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightSurface,
                unfocusedContainerColor = LightSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Composable
fun SecurityMessage() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightSurface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(painter = painterResource(id = R.drawable.shield_24px), contentDescription = "Secure", tint = Success)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Your payment information is encrypted and secure", style = MaterialTheme.typography.bodySmall)
    }
}