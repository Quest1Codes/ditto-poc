package com.quest1.demopos.data.model.orders

data class OrderItem(
    val itemId: String,
    val name: String,
    val quantity: Int,
    val cost: Int
)
