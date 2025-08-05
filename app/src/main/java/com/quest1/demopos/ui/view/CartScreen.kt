package com.quest1.demopos.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quest1.demopos.ui.components.PrimaryActionButton
import com.quest1.demopos.ui.components.QuantityControlButton
import com.quest1.demopos.ui.theme.LightTextPrimary
import com.quest1.demopos.ui.view.ShopViewModel // Assuming ShopScreen components are in the same package or imported
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    onNavigateBack: () -> Unit,
    onProceedToPayment: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartItems = uiState.itemsInCart
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "US"))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Shopping Cart", style = MaterialTheme.typography.headlineLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // AppBottomNavigationBar needs to be defined or imported
            // AppBottomNavigationBar(
            //     currentRoute = "cart",
            //     onNavigate = { if (it == "shop") onNavigateBack() },
            //     cartItemCount = uiState.cartItemCount
            // )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Your cart is empty.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(cartItems) { shopItem ->
                        CartItemCard(
                            itemName = shopItem.item.name,
                            quantity = shopItem.quantityInCart,
                            price = currencyFormat.format((shopItem.item.price ?: 0.0) * shopItem.quantityInCart),
                            onIncrease = { viewModel.updateQuantity(shopItem.item.id, 1) },
                            onDecrease = { viewModel.updateQuantity(shopItem.item.id, -1) },
                            onRemove = { viewModel.removeItemFromCart(shopItem.item.id) }
                        )
                    }
                }
                CartTotalFooter(
                    totalAmount = currencyFormat.format(uiState.cartTotal),
                    onProceedClick = onProceedToPayment
                )
            }
        }
    }
}

@Composable
fun CartItemCard(
    itemName: String,
    quantity: Int,
    price: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                spotColor = LightTextPrimary.copy(alpha = 0.05f),
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = itemName, style = MaterialTheme.typography.headlineSmall)
                QuantityControlButton(quantity = quantity, onIncrease = onIncrease, onDecrease = onDecrease)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Remove Item", tint = MaterialTheme.colorScheme.error)
                }
                Text(text = price, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CartTotalFooter(totalAmount: String, onProceedClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, spotColor = LightTextPrimary.copy(alpha = 0.1f)),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Amount", style = MaterialTheme.typography.headlineMedium)
                Text(totalAmount, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            }
            PrimaryActionButton(
                text = "Proceed to Payment",
                onClick = onProceedClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}