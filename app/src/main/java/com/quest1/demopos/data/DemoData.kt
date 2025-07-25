package com.quest1.demopos.data

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.model.payment.Gateway
import java.util.UUID

/**
 * A file to hold all stub/hardcoded data for demonstration and testing purposes.
 */

/**
 * A list of stub payment gateways.
 */
val stubGateways = listOf(
    Gateway(
        id = "stripe_21",
        name = "Stripe",
        apiEndpoint = "",
        supportedPaymentMethod = "card"
    ),
    Gateway(
        id = "paypal_45",
        name = "PayPal",
        apiEndpoint = "",
        supportedPaymentMethod = "card"
    ),
    Gateway(
        id = "adyen34",
        name = "Adyen",
        apiEndpoint = "",
        supportedPaymentMethod = "card"

    )
)

/**
 * A list of sample inventory items for the shop.
 * Used to seed the database for testing if it's empty.
 */
val sampleItems = listOf(
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_headphones",
        name = "Wireless Headphones",
        price = 15.00,
        description = "Noise-cancelling over-ear wireless headphones.",
        category = "Electronics",
        sku = "SKU2001"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_smartwatch",
        name = "Smartwatch",
        price = 28.00,
        description = "A sleek smartwatch with fitness tracking and notifications.",
        category = "Electronics",
        sku = "SKU2002"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_tshirt",
        name = "Men's Cotton T-Shirt",
        price = 25.00,
        description = "A comfortable, high-quality 100% cotton t-shirt.",
        category = "Apparel",
        sku = "SKU3001"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_jeans",
        name = "Women's Skinny Jeans",
        price = 85.00,
        description = "Modern and stylish skinny jeans with a comfortable stretch.",
        category = "Apparel",
        sku = "SKU3002"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_candle",
        name = "Scented Candle",
        price = 20.00,
        description = "A relaxing lavender-scented candle made from natural soy wax.",
        category = "Home Goods",
        sku = "SKU4001"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_mug",
        name = "Ceramic Coffee Mug",
        price = 12.00,
        description = "A large, sturdy ceramic mug, perfect for your morning coffee.",
        category = "Home Goods",
        sku = "SKU4002"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_wallet",
        name = "Leather Wallet",
        price = 45.00,
        description = "A classic bifold wallet made from genuine leather.",
        category = "Accessories",
        sku = "SKU5001"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_sunglasses",
        name = "Polarized Sunglasses",
        price = 75.00,
        description = "Stylish sunglasses with UV protection and polarized lenses.",
        category = "Accessories",
        sku = "SKU5002"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_yogamat",
        name = "Eco-Friendly Yoga Mat",
        price = 40.00,
        description = "A non-slip, eco-friendly yoga mat for your daily practice.",
        category = "Sports & Outdoors",
        sku = "SKU6001"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        itemId = "item_waterbottle",
        name = "Insulated Water Bottle",
        price = 30.00,
        description = "A 32 oz stainless steel insulated water bottle.",
        category = "Sports & Outdoors",
        sku = "SKU6002"
    )
)

/**
 * The default payment method to be used in payment requests.
 */
const val defaultPaymentMethod: String = "card"

