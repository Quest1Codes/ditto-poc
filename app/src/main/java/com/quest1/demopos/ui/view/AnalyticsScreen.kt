package com.quest1.demopos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quest1.demopos.R
import com.quest1.demopos.data.model.analytics.Acquirer
import com.quest1.demopos.data.model.analytics.StorePerformance
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.ui.theme.Error
import com.quest1.demopos.ui.theme.LightTextPrimary
import com.quest1.demopos.ui.theme.Success
import com.quest1.demopos.ui.theme.Warning
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@DrawableRes
private fun getLogoForAcquirer(acquirerName: String): Int {
    return when (acquirerName.lowercase(Locale.ROOT)) {
        "stripe" -> R.drawable.stripe_logo
        "paypal" -> R.drawable.paypal_logo
        "adyen" -> R.drawable.adyen_logo
        else -> R.drawable.credit_card_24px
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
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
                title = { Text("Store Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { LiveGatewayRankingsSection(acquirers = uiState.acquirers) }

            item {
                StorePerformanceSection(
                    performance = uiState.storePerformance,
                    transactions = uiState.recentTransactions
                )
            }
        }
    }
}

@Composable
fun StorePerformanceSection(
    performance: StorePerformance?,
    transactions: List<Transaction>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Transaction Information (Today)",
            style = MaterialTheme.typography.headlineSmall
        )
        // Performance Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Total Transactions",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        performance?.totalTransactions?.toString() ?: "0",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        performance?.transactionGrowth ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Revenue Today",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        performance?.revenueToday ?: "$0",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        performance?.revenueGrowth ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Recent Transactions Card
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium
                )

                transactions.forEach { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val date = Date(transaction.createdAt)
    val dateFormat = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(date)

    val format = NumberFormat.getCurrencyInstance(Locale.US)
    format.currency = java.util.Currency.getInstance(transaction.currency)
    val amount = format.format(transaction.amount)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    transaction.id,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    amount,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    transaction.status,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (transaction.status == "SUCCESS") Success else Error
                )
            }
        }
    }
}

@Composable
fun LiveGatewayRankingsSection(acquirers: List<Acquirer>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Gateway Rankings",
            style = MaterialTheme.typography.headlineSmall
        )

        acquirers.forEach { acquirer ->
            AcquirerRankingCard(acquirer = acquirer)
        }
    }
}

@Composable
fun AcquirerRankingCard(acquirer: Acquirer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            LightTextPrimary, // <-- MODIFIED COLOR
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#${acquirer.rank}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // MODIFIED: Replaced Text with Image for the logo
                Image(
                    painter = painterResource(id = getLogoForAcquirer(acquirer.name)),
                    contentDescription = "${acquirer.name} Logo",
                    modifier = Modifier.height(24.dp) // Adjust height as needed
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Avg. Latency: ${acquirer.latency}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Icon(
                        painter = painterResource(id = acquirer.statusInfo.iconRes),
                        contentDescription = "Gateway Status",
                        tint = acquirer.statusInfo.color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    "Success Rate: ${acquirer.successRate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}