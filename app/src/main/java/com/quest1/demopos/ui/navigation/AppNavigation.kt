package com.quest1.demopos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quest1.demopos.ui.view.*
import androidx.navigation.navArgument

object AppRoutes {
    const val AUTH = "auth"
    const val SHOP = "shop"
    const val CART = "cart"
    const val PAYMENT_GATEWAY = "payment_gateway"
    const val PAYMENT = "payment"

    // NEW
    const val ANALYTICS = "analytics"
    const val PAYMENT_DASHBOARD = "payment_dashboard"
    const val PRESENCE_VIEWER = "presence_viewer"
}

/**
 * The main navigation component for the application.
 * Manages the navigation between different screens.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val shopViewModel: ShopViewModel = hiltViewModel()

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    val startDestination = remember(authState) {
        if (authState is AuthState.Success) {
            AppRoutes.SHOP
        } else {
            AppRoutes.AUTH
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppRoutes.AUTH) {
            AuthScreen(
                onLoginSuccess = { role ->
                    navController.navigate(AppRoutes.SHOP) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.SHOP) {
            val shopViewModel = hiltViewModel<ShopViewModel>()
            ShopScreen(
                viewModel = shopViewModel,
                onNavigateToCart = { navController.navigate(AppRoutes.CART) },
                navController = navController
            )
        }
        composable(AppRoutes.CART) {
            val uiState = shopViewModel.uiState.collectAsState().value
            CartScreen(
                viewModel = shopViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProceedToPayment = {
                    val totalAmount = uiState.cartTotal
                    val orderId = uiState.activeOrderId
                    if (orderId != null) {
                        navController.navigate("${AppRoutes.PAYMENT_GATEWAY}/$totalAmount/$orderId")
                    }
                }
            )
        }

        composable(
            route = "${AppRoutes.PAYMENT_GATEWAY}/{totalAmount}/{orderId}",
            arguments = listOf(
                navArgument("totalAmount") { type = NavType.StringType },
                navArgument("orderId") { type = NavType.StringType }
            )
        ) {
            PaymentGatewayScreen(
                onNavigateBack = { navController.popBackStack() },
                onPayNowClicked = {
                    navController.navigate(AppRoutes.PAYMENT)
                }
            )
        }

        composable(AppRoutes.PAYMENT) {
            val paymentViewModel: PaymentViewModel = hiltViewModel()
            PaymentScreen(
                shopViewModel = shopViewModel,
                viewModel = paymentViewModel,
                onNavigateBack = {
                    paymentViewModel.reset()
                    navController.popBackStack()
                },
                onNavigateHome = {
                    navController.navigate(AppRoutes.SHOP) {
                        popUpTo(AppRoutes.SHOP) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.ANALYTICS) {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.PAYMENT_DASHBOARD) {
            PaymentDashboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.PRESENCE_VIEWER) {
            PresenceViewer(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}