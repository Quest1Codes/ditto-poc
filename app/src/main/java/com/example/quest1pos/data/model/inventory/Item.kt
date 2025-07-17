package com.example.quest1pos.data.model.inventory

data class Item(
    val id: String,
    val itemId: String,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val sku: String
)
