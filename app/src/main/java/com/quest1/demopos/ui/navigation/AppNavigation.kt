package com.quest1.demopos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    const val PAYMENT_GATEWAY = "payment_gateway" // New Route
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

    // The ShopViewModel is shared between the Shop and Cart screens
    val shopViewModel: ShopViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = AppRoutes.AUTH) {
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
                navController = navController // This line was missing
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
                    // Navigate to the new Payment Gateway Screen
                    val totalAmount = uiState.cartTotal
                    val orderNumber = (1000000..9999999).random() // Generate a random order number
                    navController.navigate("${AppRoutes.PAYMENT_GATEWAY}/$totalAmount/$orderNumber")
                }
            )
        }

        // New composable for the Payment Gateway screen
        composable(
            route = "${AppRoutes.PAYMENT_GATEWAY}/{totalAmount}/{orderNumber}",
            arguments = listOf(
                navArgument("totalAmount") { type = NavType.StringType },
                navArgument("orderNumber") { type = NavType.StringType }
            )
        ) {
            PaymentGatewayScreen(
                onNavigateBack = { navController.popBackStack() },
                onPayNowClicked = {
                    // After clicking Pay Now, navigate to the final processing screen
                    navController.navigate(AppRoutes.PAYMENT)
                }
            )
        }

        composable(AppRoutes.PAYMENT) {
            val paymentViewModel: PaymentViewModel = hiltViewModel()
            PaymentScreen(
                shopViewModel = shopViewModel, // Pass the ShopViewModel instance
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