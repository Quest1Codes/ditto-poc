package com.quest1.demopos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quest1.demopos.R
import com.quest1.demopos.ui.components.PrimaryActionButton
import com.quest1.demopos.ui.theme.Error
import com.quest1.demopos.ui.theme.Success

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    shopViewModel: ShopViewModel,
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startPaymentProcess()
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateHome()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState.status) {
            PaymentStatus.SELECTING_GATEWAY -> PaymentStatusComponent(
                title = "Fetching the best Gateway...",
                subtitle = "Please wait while we find the most optimal payment route for you.",
                content = { CircularProgressIndicator() }
            )
            PaymentStatus.INITIATING -> PaymentStatusComponent(
                iconRes = R.drawable.credit_card_24px,
                title = "Initiating Payment...",
                subtitle = "You are being redirected to our secure payment partner. Please do not click back or exit.",
                logoRes = uiState.acquirerLogoRes
            )
            PaymentStatus.PROCESSING -> PaymentStatusComponent(
                title = "Processing Your Payment",
                subtitle = "Please wait, this may take a moment...",
                logoRes = uiState.acquirerLogoRes,
                content = {
                    val animatedProgress by animateFloatAsState(targetValue = uiState.progress, label = "progress")
                    CircularProgressIndicator(progress = { animatedProgress })
                }

            )
            PaymentStatus.SUCCESSFUL -> PaymentStatusComponent(
                iconRes = R.drawable.credit_score_24px,
                iconTint = Success,
                title = "Payment Successful!",
                subtitle = "Your payment has been processed successfully.",
                logoRes = uiState.acquirerLogoRes,
                content = {
                    PrimaryActionButton(text = "View Receipt", onClick = {
                        viewModel.startRedirectHome()
                        onNavigateHome()
                    })
                }
            )
            PaymentStatus.FAILED -> PaymentStatusComponent(
                iconRes = R.drawable.credit_card_off_24px,
                iconTint = Error,
                title = "Payment Not Successful",
                subtitle = "There was an issue processing your payment.",
                logoRes = uiState.acquirerLogoRes,
                content = {
                    PrimaryActionButton(
                        text = "Please Try Again",
                        onClick = { viewModel.startPaymentProcess() }
                    )
                }
            )
            PaymentStatus.REDIRECTING -> PaymentStatusComponent(
                iconRes = R.drawable.home_24px,
                title = "Redirecting...",
                subtitle = "You will be returned to the home screen shortly."
            )
        }
    }
}

@Composable
fun PaymentStatusComponent(
    title: String,
    subtitle: String,
    @DrawableRes iconRes: Int? = null,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    @DrawableRes logoRes: Int? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp), // Increased icon size
                    tint = iconTint
                )
                Spacer(Modifier.height(24.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            if (content != null) {
                Spacer(Modifier.height(32.dp))
                content()
            }
        }

        if (logoRes != null) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "Payment Gateway Logo",
                modifier = Modifier
                    .height(76.dp)
                    .padding(bottom = 32.dp),
                colorFilter = null
            )
        }
    }
}