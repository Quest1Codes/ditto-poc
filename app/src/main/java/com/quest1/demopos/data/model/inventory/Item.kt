package com.quest1.demopos.data.model.inventory

data class Item(
    val id: String,
    val itemId: String,
    val name: String,
    val price: Double,
    val description: String,
    val category: String,
    val sku: String
)
{
    companion object {
        // Define the collection name here to ensure consistency across the app.
        const val COLLECTION_NAME = "items"
    }
}

