// File: app/src/main/java/com/quest1/demopos/ui/view/PaymentDashboardScreen.kt
package com.quest1.demopos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quest1.demopos.R
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.ui.theme.Error
import com.quest1.demopos.ui.theme.LightTextPrimary
import com.quest1.demopos.ui.theme.Success
import com.quest1.demopos.ui.theme.Warning
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@DrawableRes
private fun getLogoForAcquirer(acquirerName: String): Int {
    return when (acquirerName.lowercase(Locale.ROOT)) {
        "stripe" -> R.drawable.stripe_logo
        "paypal" -> R.drawable.paypal_logo
        "adyen" -> R.drawable.adyen_logo
        else -> R.drawable.credit_card_24px
    }
}

private fun getLogoHeightForAcquirer(acquirerName: String): Dp {
    return when (acquirerName.lowercase(Locale.ROOT)) {
        "adyen" -> 18.dp
        "paypal" -> 20.dp
        "stripe" -> 26.dp
        else -> 24.dp
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDashboardScreen(
    onNavigateBack: () -> Unit,
    viewModel: PaymentDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Payment Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.transactions) { transaction ->
                    PaymentCardItem(
                        transaction = transaction
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentCardItem(
    transaction: Transaction
) {
    val date = Date(transaction.createdAt)
    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(date)
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    format.currency = java.util.Currency.getInstance(transaction.currency)
    val amount = format.format(transaction.amount)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = getLogoForAcquirer(transaction.acquirerName)),
                        contentDescription = transaction.acquirerName,
                        modifier = Modifier.height(getLogoHeightForAcquirer(transaction.acquirerName))
                    )
                    Text(
                        text = transaction.id,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                PaymentStatusBadge(status = transaction.status)
            }

            Text(
                text = amount,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = LightTextPrimary,
                modifier = Modifier.align(Alignment.End)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.schedule_24px),
                    contentDescription = "Time",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun PaymentStatusBadge(status: String) {
    val (backgroundColor, textColor, text) = when (status) {
        "SUCCESS" -> Triple(Success.copy(alpha = 0.1f), Success, "Success")
        "FAILED" -> Triple(Error.copy(alpha = 0.1f), Error, "Failed")
        "PENDING" -> Triple(Warning.copy(alpha = 0.1f), Warning, "Pending")
        else -> Triple(Color.Gray.copy(alpha = 0.1f), Color.Gray, status)
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}