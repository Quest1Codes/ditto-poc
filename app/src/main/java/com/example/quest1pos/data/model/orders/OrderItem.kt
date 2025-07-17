package com.example.quest1pos.data.model.orders

data class OrderItem(
    val itemId: String,
    val name: String,
    val quantity: Int,
    val cost: Int
)
