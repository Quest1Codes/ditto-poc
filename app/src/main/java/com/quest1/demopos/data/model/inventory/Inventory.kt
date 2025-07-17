package com.quest1.demopos.data.model.inventory

data class Inventory(
    val id: Int,
    val itemId: String,
    val inventoryQuantity: Int,
    val supplierId: String
)
