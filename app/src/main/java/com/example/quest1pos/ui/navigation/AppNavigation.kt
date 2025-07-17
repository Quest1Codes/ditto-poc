package com.example.quest1pos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quest1pos.ui.view.CartScreen
import com.example.quest1pos.ui.view.PaymentScreen
import com.example.quest1pos.ui.view.PaymentViewModel
import com.example.quest1pos.ui.view.ShopScreen
import com.example.quest1pos.ui.view.ShopViewModel

object AppRoutes {
    const val SHOP = "shop"
    const val CART = "cart"
    const val PAYMENT = "payment"
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
                }
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
    }
}
