package com.quest1.demopos.data.repository
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import live.ditto.Ditto
import live.ditto.ditto_wrapper.DittoManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.flowOf

@Singleton
class DittoRepository @Inject constructor(
    private val dittoManager: DittoManager
) {
    private val TAG = "DittoRepository"

    private val ditto: Ditto by lazy {
        dittoManager.requireDitto()
    }

    /**
     * Observes a Ditto collection for changes based on a DQL query.
     * This function returns a Flow that emits a list of documents whenever the query results change.
     *
     * @param query The DQL SELECT statement to execute.
     * @param arguments Optional arguments for the DQL query.
     * @return A Flow emitting a list of documents as Map<String, Any?>.
     */
    fun observeCollection(
        query: String,
        arguments: Map<String, Any?> = emptyMap()
    ): Flow<List<Map<String, Any?>>> {
        Log.d(TAG, "Setting up STUBBED observer for query: $query")

        // 1. Define sample data that mimics the real document structure.
        val sampleData = listOf(
            mapOf("id" to "item_1", "name" to "Stubbed Product Alpha", "price" to 19.99),
            mapOf("id" to "item_2", "name" to "Stubbed Product Beta", "price" to 25.50, "inStock" to true)
        )

        // 2. Use `flowOf` to create a Flow that emits the sample list just once.
        return flowOf(sampleData)
    }

    /**
     * Executes a write operation (INSERT, UPDATE, DELETE) using a DQL query.
     * This is a suspend function as it performs a database operation.
     *
     * @param query The DQL mutation statement (e.g., INSERT INTO...).
     * @param arguments Optional arguments for the DQL query.
     */
    suspend fun executeQuery(query: String, arguments: Map<String, Any?> = emptyMap()) {
        try {
            Log.d(TAG, "Executing query: $query")
            ditto.store.execute(query, arguments)
        } catch (e: Exception) {
            Log.e(TAG, "Error executing DQL query: $query", e)
        }
    }

    /**
     * Sets up a subscription to sync data from the mesh for a given query.
     * This tells Ditto which data this device is interested in receiving from other peers.
     *
     * @param query The DQL SELECT statement for the subscription.
     */
    fun startSubscription(query: String) {
        Log.d(TAG, "Starting subscription for query: $query")
        ditto.sync.registerSubscription(query)
    }
}