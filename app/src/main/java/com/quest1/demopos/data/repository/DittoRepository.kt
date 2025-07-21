/*
 * File: app/src/main/java/com/quest1/demopos/data/repository/DittoRepository.kt
 * Description: Corrected the `observeCollection` function to use `result.items`.
 * - This resolves the "Unresolved reference 'documents'" build error.
 */
package com.quest1.demopos.data.repository

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import live.ditto.Ditto
import live.ditto.DittoQueryResult
import live.ditto.DittoStoreObserver
import live.ditto.ditto_wrapper.DittoManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DittoRepository @Inject constructor(
    private val dittoManager: DittoManager
) {

    private val TAG = "DittoRepository"
    private val ditto: Ditto by lazy {
        dittoManager.requireDitto()
    }

    fun observeCollection(
        query: String,
        arguments: Map<String, Any?> = emptyMap()
    ): Flow<List<Map<String, Any?>>> = callbackFlow {
        Log.d(TAG, "Setting up REAL observer for query: $query")
        val observer: DittoStoreObserver = ditto.store.registerObserver(query, arguments) { result: DittoQueryResult ->
            // CORRECTED LINE: Use `result.items` to get the documents.
            val docMaps = result.items.map { it.value }
            trySend(docMaps)
        }
        awaitClose {
            Log.d(TAG, "Stopping observer for query: $query")
            observer.close()
        }
    }

    suspend fun upsert(collection: String, document: Map<String, Any?>) {
        try {
            Log.d(TAG, "Upserting into $collection: $document")
            ditto.store.collection(collection).upsert(document)
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting document", e)
            throw e
        }
    }

    suspend fun executeQuery(query: String, arguments: Map<String, Any?> = emptyMap()) {
        try {
            Log.d(TAG, "Executing query: $query with args: $arguments")
            ditto.store.execute(query, arguments)
        } catch (e: Exception) {
            Log.e(TAG, "Error executing DQL query: $query", e)
            throw e
        }
    }

    fun startSubscription(query: String) {
        Log.d(TAG, "Starting subscription for query: $query")
        ditto.sync.registerSubscription(query)
    }
}
