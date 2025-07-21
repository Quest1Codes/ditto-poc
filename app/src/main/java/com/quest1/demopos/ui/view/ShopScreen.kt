package com.quest1.demopos.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.quest1.demopos.ui.components.PrimaryActionButton
import com.quest1.demopos.ui.components.ProductItemCard
import com.quest1.demopos.ui.navigation.AppRoutes
import com.quest1.demopos.ui.theme.LightTextPrimary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    onNavigateToCart: () -> Unit,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Shop", style = MaterialTheme.typography.headlineLarge) },
                actions = {
                    var expanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Store Analytics", style = MaterialTheme.typography.bodySmall, color = Color.Black) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Analytics,
                                    contentDescription = "Analytics Icon"
                                )
                            },
                            onClick = {
                                expanded = false
                                navController.navigate(AppRoutes.ANALYTICS)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Payment Dashboard", style = MaterialTheme.typography.bodySmall, color = Color.Black) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.CreditCard,
                                    contentDescription = "Payment Icon"
                                )
                            },
                            onClick = {
                                expanded = false
                                navController.navigate(AppRoutes.PAYMENT_DASHBOARD)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // This is now the main navigation bar
            AppBottomNavigationBar(
                currentRoute = "shop",
                onNavigate = { if (it == "cart") onNavigateToCart() },
                cartItemCount = uiState.cartItemCount
            )
        },
        floatingActionButton = {
            // The checkout bar is now a Floating Action Button
            if (uiState.cartItemCount > 0) {
                FloatingCheckoutBar(
                    totalAmount = currencyFormat.format(uiState.cartTotal),
                    itemCount = uiState.cartItemCount,
                    onCheckoutClick = onNavigateToCart
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp) // Extra padding for FAB
            ) {
                items(uiState.items) { shopItem ->
                    ProductItemCard(
                        itemName = shopItem.item.name,
                        price = currencyFormat.format(shopItem.item.price),
                        quantity = shopItem.quantityInCart,
                        onIncrease = { viewModel.updateQuantity(shopItem.item.id, 1) },
                        onDecrease = { viewModel.updateQuantity(shopItem.item.id, -1) }
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingCheckoutBar(totalAmount: String, itemCount: Int, onCheckoutClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .shadow(
                elevation = 12.dp,
                spotColor = LightTextPrimary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.extraLarge
            ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = LightTextPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(totalAmount, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Text("$itemCount items", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            }
            PrimaryActionButton(
                text = "Checkout",
                onClick = onCheckoutClick,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@Composable
fun AppBottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit, cartItemCount: Int) {
    NavigationBar(
        modifier = Modifier.shadow(elevation = 12.dp, spotColor = LightTextPrimary.copy(alpha = 0.1f)),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NavigationBarItem(
            selected = currentRoute == "shop",
            onClick = { onNavigate("shop") },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = { onNavigate("cart") },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge { Text(cartItemCount.toString()) }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Cart")
                }
            },
            label = { Text("Cart") }
        )
    }
}
