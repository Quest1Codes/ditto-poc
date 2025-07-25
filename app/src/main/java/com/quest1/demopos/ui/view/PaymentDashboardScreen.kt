package com.quest1.demopos.ui.view

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.ui.theme.Success
import com.quest1.demopos.ui.theme.Error
import com.quest1.demopos.ui.theme.Warning
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                columns = GridCells.Fixed(2), // 2 columns for tablets, you can use GridCells.Adaptive(180.dp) for responsive
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

    val format = NumberFormat.getCurrencyInstance(Locale.US) // or use currency code
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
            // Header with acquirer and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.acquirerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = transaction.id,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                PaymentStatusBadge(status = transaction.status)
            }

            // Amount
            Text(
                text = amount,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Date, Time & Location
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
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
