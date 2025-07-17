package com.example.quest1pos.data.repository

import com.example.quest1pos.data.model.core.Coordinates
import com.example.quest1pos.data.model.core.Location
import com.example.quest1pos.data.model.core.Organization
import com.example.quest1pos.data.model.core.Store
import com.example.quest1pos.data.model.core.Terminal
import com.example.quest1pos.data.model.core.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoreRepository @Inject constructor() {

    // --- Stub Data ---
    private val stubOrganization = Organization(id = "org_123", organizationName = "Quest1 Retail Inc.")

    private val stubStores = listOf(
        Store(
            id = "store_01",
            name = "Downtown Flagship",
            organizationId = 123,
            location = Location(
                address = "123 Main St",
                city = "Metropolis",
                country = "USA",
                coordinates = Coordinates(type = "Point", coordinates = listOf(-74.0060, 40.7128))
            )
        ),
        Store(
            id = "store_02",
            name = "Uptown Branch",
            organizationId = 123,
            location = Location(
                address = "456 Oak Ave",
                city = "Metropolis",
                country = "USA",
                coordinates = Coordinates(type = "Point", coordinates = listOf(-73.9654, 40.7829))
            )
        )
    )

    private val stubUsers = listOf(
        User(id = "user_jane", displayName = "Jane Doe", role = "MANAGER", phoneNumber = 1234567890L),
        User(id = "user_john", displayName = "John Smith", role = "CASHIER", phoneNumber = 9876543210L)
    )

    private val stubTerminals = listOf(
        Terminal(id = "term_A1", storeId = "store_01", name = "Front Counter 1", ipAddress = "192.168.1.10", lastSeen = System.currentTimeMillis()),
        Terminal(id = "term_A2", storeId = "store_01", name = "Front Counter 2", ipAddress = "192.168.1.11", lastSeen = System.currentTimeMillis())
    )

    // --- Repository Functions ---

    fun getOrganization(): Flow<Organization> = flow {
        emit(stubOrganization)
    }

    fun getStores(): Flow<List<Store>> = flow {
        emit(stubStores)
    }

    fun getUsersForStore(storeId: String): Flow<List<User>> = flow {
        // In a real app, you'd filter by storeId
        emit(stubUsers)
    }

    fun getTerminalsForStore(storeId: String): Flow<List<Terminal>> = flow {
        emit(stubTerminals.filter { it.storeId == storeId })
    }
}