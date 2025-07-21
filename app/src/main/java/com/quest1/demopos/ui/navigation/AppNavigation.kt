package com.quest1.demopos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quest1.demopos.ui.view.AnalyticsScreen
import com.quest1.demopos.ui.view.CartScreen
import com.quest1.demopos.ui.view.PaymentDashboardScreen
import com.quest1.demopos.ui.view.PaymentScreen
import com.quest1.demopos.ui.view.PaymentViewModel
import com.quest1.demopos.ui.view.ShopScreen
import com.quest1.demopos.ui.view.ShopViewModel

object AppRoutes {
    const val SHOP = "shop"
    const val CART = "cart"
    const val PAYMENT = "payment"

    // NEW
    const val ANALYTICS = "analytics"
    const val PAYMENT_DASHBOARD = "payment_dashboard"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // The ShopViewModel is shared between the Shop and Cart screens
    val shopViewModel: ShopViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = AppRoutes.SHOP) {
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
                    // Navigate back to the shop screen, clearing the back stack
                    navController.navigate(AppRoutes.SHOP) {
                        popUpTo(AppRoutes.SHOP) { inclusive = true }
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
    }
}
