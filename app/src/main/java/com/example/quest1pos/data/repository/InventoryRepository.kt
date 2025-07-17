package com.example.quest1pos.data.repository

import com.example.quest1pos.data.model.inventory.Inventory
import com.example.quest1pos.data.model.inventory.Item
import com.example.quest1pos.data.model.inventory.Supplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor() {

    // --- Stub Data ---
    private val stubItems = listOf(
        Item(id = "item_burger", itemId = "item_burger", name = "Classic Burger", price = 15.00, description = "A delicious all-beef burger.", category = "Entrees", sku = "SKU1001"),
        Item(id = "item_fries", itemId = "item_fries", name = "Large Fries", price = 5.00, description = "Crispy golden fries.", category = "Sides", sku = "SKU1002"),
        Item(id = "item_coffee", itemId = "item_coffee", name = "Latte", price = 6.00, description = "Freshly brewed espresso with steamed milk.", category = "Beverages", sku = "SKU1003"),
        Item(id = "item_dosa", itemId = "item_dosa", name = "Dosa", price = 60.00, description = "Freshly brewed espresso with steamed milk.", category = "Food", sku = "SKU1004")
    )

    private val stubInventory = listOf(
        Inventory(id = 1, itemId = "item_burger", inventoryQuantity = 100, supplierId = "sup_foodco"),
        Inventory(id = 2, itemId = "item_fries", inventoryQuantity = 250, supplierId = "sup_foodco"),
        Inventory(id = 3, itemId = "item_coffee", inventoryQuantity = 50, supplierId = "sup_beverages"),
        Inventory(id = 4, itemId = "item_dosa", inventoryQuantity = 100, supplierId = "sup_beverages")
    )

    private val stubSuppliers = listOf(
        Supplier(id = 1, supplierName = "FoodCo Inc.", storeId = 1),
        Supplier(id = 2, supplierName = "Global Beverages", storeId = 1)
    )

    // --- Repository Functions ---

    fun getAvailableItems(): Flow<List<Item>> = flow {
        emit(stubItems)
    }

    fun getInventoryForItem(itemId: String): Flow<Inventory?> = flow {
        emit(stubInventory.find { it.itemId == itemId })
    }

    fun getSuppliersForStore(storeId: String): Flow<List<Supplier>> = flow {
        // In a real app, you'd filter by storeId
        emit(stubSuppliers)
    }
}