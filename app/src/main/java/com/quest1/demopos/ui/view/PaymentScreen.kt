package com.quest1.demopos.ui.view

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    shopViewModel: ShopViewModel, // Accept the ShopViewModel
    viewModel: PaymentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val shopUiState by shopViewModel.uiState.collectAsState()

    // Pass the cart data to the payment process when the screen launches
    LaunchedEffect(Unit) {
        viewModel.startPaymentProcess()
    }

    // This effect listens for navigation events from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onNavigateHome()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    // The payment flow is a modal experience
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState.status) {
            PaymentStatus.INITIATING -> PaymentStatusComponent(
                iconRes = R.drawable.credit_card_24px, // Use credit_card icon
                title = "Initiating Payment...",
                subtitle = "You are being redirected to our secure payment partner. Please do not click back or exit."
            )
            PaymentStatus.PROCESSING -> PaymentStatusComponent(
                iconRes = null, // No icon for processing
                title = "Processing Your Payment",
                subtitle = "Please wait, this may take a moment...",
                content = {
                    val animatedProgress by animateFloatAsState(targetValue = uiState.progress,
                        label = "progress"
                    )
                    CircularProgressIndicator(progress = { animatedProgress })
                }
            )
            PaymentStatus.SUCCESSFUL -> PaymentStatusComponent(
                iconRes = R.drawable.credit_score_24px, // Use credit_score icon
                iconTint = Success,
                title = "Payment Successful!",
                subtitle = "Your payment has been processed successfully.",
                content = {
                    PrimaryActionButton(text = "View Receipt", onClick = {
                        viewModel.startRedirectHome()
                        onNavigateHome() // Trigger navigation
                    })
                }
            )
            PaymentStatus.FAILED -> PaymentStatusComponent(
                iconRes = R.drawable.credit_card_off_24px, // Use credit_card_off icon
                iconTint = Error,
                title = "Payment Not Successful",
                subtitle = "There was an issue processing your payment.",
                content = {
                    PrimaryActionButton(
                        text = "Please Try Again",
                        onClick = { viewModel.startPaymentProcess() }
                    )
                }
            )
            PaymentStatus.REDIRECTING -> PaymentStatusComponent(
                iconRes = R.drawable.home_24px, // Assuming you have a home icon in drawables
                title = "Redirecting...",
                subtitle = "You will be returned to the home screen shortly."
            )
        }
    }
}

@Composable
fun PaymentStatusComponent(
    @DrawableRes iconRes: Int?, // Changed to accept a Drawable Resource ID
    title: String,
    subtitle: String,
    iconTint: Color = MaterialTheme.colorScheme.onBackground,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (iconRes != null) { // Check if the resource ID is provided
            Icon(
                painter = painterResource(id = iconRes), // Use the passed-in icon resource
                contentDescription = null,
                modifier = Modifier.size(48.dp),
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
}
