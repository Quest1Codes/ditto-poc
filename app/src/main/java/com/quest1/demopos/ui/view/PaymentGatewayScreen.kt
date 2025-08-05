// [Source: 1937]
package com.quest1.demopos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quest1.demopos.R
import com.quest1.demopos.ui.components.PrimaryActionButton
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs


private fun formatCurrency(amount: Double, isDiscount: Boolean = false): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "US")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
        currency = java.util.Currency.getInstance("USD")
    }
    val formatted = format.format(abs(amount))
    return if (isDiscount) "-$formatted" else formatted
}


@DrawableRes
private fun getLogoForProcessor(processorName: String): Int {
    return when (processorName.lowercase(Locale.ROOT)) {
        "stripe" -> R.drawable.stripe_logo
        "paypal" -> R.drawable.paypal_logo
        "adyen" -> R.drawable.adyen_logo
        else -> R.drawable.credit_card_24px
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewayScreen(
    onNavigateBack: () -> Unit,
    onPayNowClicked: () -> Unit,
    viewModel: PaymentGatewayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    Scaffold(
        bottomBar = {
            PrimaryActionButton(
                text = "Confirm Payment",
                onClick = onPayNowClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    ) { paddingValues ->
        if (uiState.isLoadingGateway) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Fetching the best gateway...")
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(64.dp))
                TotalOrderHeader(totalAmount = uiState.totalAmount)
                Spacer(Modifier.height(96.dp))
                CreditCardLayout(
                    cardHolderName = uiState.cardHolderName
                )
                Spacer(Modifier.height(32.dp))
                PaymentInfoSection(uiState = uiState, viewModel = viewModel)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                ExpandableOrderItems(uiState = uiState)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                OrderTotalSummary(uiState = uiState)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}


@Composable
private fun TotalOrderHeader(totalAmount: Double) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TOTAL ORDER",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = formatCurrency(totalAmount),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CreditCardLayout(cardHolderName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)) {
            Image(
                painter = painterResource(id = R.drawable.icons8_nfc_100),
                contentDescription = "Contactless",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.icons8_chip_card_100),
                contentDescription = "Card Chip",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(bottom = 10.dp)
                    .width(72.dp)
            )
            Text(
                text = cardHolderName,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
            Image(
                painter = painterResource(id = R.drawable.mastercard_logo),
                contentDescription = "Mastercard Logo",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(40.dp)
            )
        }
    }
}


@Composable
private fun PaymentInfoSection(uiState: PaymentGatewayUiState, viewModel: PaymentGatewayViewModel) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Saved Cards",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.mastercard_logo),
                contentDescription = "Card Logo",
                modifier = Modifier.height(28.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "•••• •••• •••• ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 3.dp, end = 4.dp)
                    )
                    Text(
                        text = uiState.cardLastFour,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = uiState.lastUsedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            RadioButton(
                selected = uiState.isCardSelected,
                onClick = { viewModel.onCardSelectionChanged() },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun ExpandableOrderItems(uiState: PaymentGatewayUiState) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ORDER DETAILS",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
            Icon(
                painter = if (expanded) painterResource(R.drawable.unfold_less_24px)
                else painterResource(R.drawable.unfold_more_24px),
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = Color.Gray,
            )
        }
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                if (uiState.orderItems.isEmpty()) {
                    Text("No items in this order.", color = Color.Gray)
                } else {
                    uiState.orderItems.forEach { item ->
                        OrderDetailRow(
                            label = "${item.name} x ${item.quantity}",
                            value = formatCurrency(item.cost.toDouble() * item.quantity),
                            isLast = item == uiState.orderItems.last()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderTotalSummary(uiState: PaymentGatewayUiState) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OrderDetailRow(label = "Item Total", value = formatCurrency(uiState.itemTotal))
        OrderDetailRow(label = "Taxes", value = formatCurrency(uiState.taxes))
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Order",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatCurrency(uiState.totalAmount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OrderDetailRow(label: String, value: String, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}