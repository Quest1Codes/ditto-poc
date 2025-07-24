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
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // The ShopViewModel is shared between the Shop, Cart, and Payment screens
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
            ShopScreen(
                viewModel = shopViewModel,
                onNavigateToCart = {
                    navController.navigate(AppRoutes.CART)
                }
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
                    // Navigate back to the shop screen, clearing the back stack
                    navController.navigate(AppRoutes.SHOP) {
                        popUpTo(AppRoutes.SHOP) { inclusive = true }
                    }
                }
            )
        }
    }
}