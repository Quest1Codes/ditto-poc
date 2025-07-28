package com.quest1.demopos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quest1.demopos.ui.view.*

/**
 * Defines the routes for the application.
 */
object AppRoutes {
    const val AUTH = "auth"
    const val SHOP = "shop"
    const val CART = "cart"
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

    // The NavHost defines the navigation graph.
    // The startDestination is now the authentication screen.
    NavHost(navController = navController, startDestination = AppRoutes.AUTH) {
        composable(AppRoutes.AUTH) {
            AuthScreen(
                onLoginSuccess = { role ->
                    // After a successful login, navigate to the main shop screen.
                    // The back stack is cleared to prevent the user from going back to the login screen.
                    navController.navigate(AppRoutes.SHOP) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.SHOP) {
            ShopScreen(
                viewModel = shopViewModel,
                onNavigateToCart = {
                    navController.navigate(AppRoutes.CART)
                },
                navController
            )
        }

        composable(AppRoutes.CART) {
            CartScreen(
                viewModel = shopViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProceedToPayment = {
                    navController.navigate(AppRoutes.PAYMENT)
                }
            )
        }

        composable(AppRoutes.PAYMENT) {
            val paymentViewModel: PaymentViewModel = hiltViewModel()
            PaymentScreen(
                viewModel = paymentViewModel,
                onNavigateBack = {
                    paymentViewModel.reset()
                    navController.popBackStack()
                },
                onNavigateHome = {
                    // CORRECTED NAVIGATION LOGIC:
                    // This correctly navigates to the Shop screen and clears the
                    // Cart and Payment screens from the back stack.
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