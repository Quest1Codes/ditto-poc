package com.quest1.demopos.data.repository

import android.util.Log
import com.quest1.demopos.data.model.analytics.GatewayPerformance
import com.quest1.demopos.data.model.analytics.ditto.GATEWAY_PERFORMANCE_COLLECTION_NAME
import com.quest1.demopos.data.model.analytics.ditto.GetAllGatewayPerformanceQuery
import com.quest1.demopos.data.model.analytics.ditto.UpsertGatewayPerformanceQuery
import com.quest1.demopos.data.model.core.Terminal
import com.quest1.demopos.data.model.core.ditto.TERMINALS_COLLECTION_NAME
import com.quest1.demopos.data.model.core.ditto.UpsertTerminalQuery
import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.model.inventory.ditto.GetAllItemsQuery
import com.quest1.demopos.data.model.inventory.ditto.GetItemByIdQuery
import com.quest1.demopos.data.model.inventory.ditto.ITEMS_COLLECTION_NAME
import com.quest1.demopos.data.model.inventory.ditto.InsertItemQuery
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.model.orders.ditto.DeleteOrderQuery
import com.quest1.demopos.data.model.orders.ditto.GetActiveOrderQuery
import com.quest1.demopos.data.model.orders.ditto.GetAllTransactionsQuery
import com.quest1.demopos.data.model.orders.ditto.GetOrderByIdQuery
import com.quest1.demopos.data.model.orders.ditto.InsertTransactionQuery
import com.quest1.demopos.data.model.orders.ditto.ORDERS_COLLECTION_NAME
import com.quest1.demopos.data.model.orders.ditto.TRANSACTIONS_COLLECTION_NAME
import com.quest1.demopos.data.model.orders.ditto.UpsertOrderQuery
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import live.ditto.Ditto
import live.ditto.DittoQueryResult
import live.ditto.DittoStoreObserver
import live.ditto.ditto_wrapper.DittoManager
import live.ditto.ditto_wrapper.DittoStoreManager
import live.ditto.ditto_wrapper.dittowrappers.DittoCollectionSubscription
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DittoRepository @Inject constructor(
    private val dittoManager: DittoManager,
    private val dittoStoreManager: DittoStoreManager
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

    init {
        startInventorySubscription()
        startOrdersSubscription()
        startTransactionsSubscription()
        startTerminalsSubscription()
        startGatewayPerformanceSubscription()
    }

    private fun startInventorySubscription() {
        val subscription = object : DittoCollectionSubscription {
            override val collectionName = ITEMS_COLLECTION_NAME
            override val subscriptionQuery = "SELECT * FROM $ITEMS_COLLECTION_NAME"
            override val subscriptionQueryArgs = emptyMap<String, Any>()
            override val evictionQuery = ""
        }
        dittoStoreManager.registerSubscription(subscription)
    }

    private fun startOrdersSubscription() {
        val subscription = object : DittoCollectionSubscription {
            override val collectionName = ORDERS_COLLECTION_NAME
            override val subscriptionQuery = "SELECT * FROM $ORDERS_COLLECTION_NAME"
            override val subscriptionQueryArgs = emptyMap<String, Any>()
            override val evictionQuery = ""
        }
        dittoStoreManager.registerSubscription(subscription)
    }

    private fun startTransactionsSubscription() {
        val subscription = object : DittoCollectionSubscription {
            override val collectionName = TRANSACTIONS_COLLECTION_NAME
            override val subscriptionQuery = "SELECT * FROM $TRANSACTIONS_COLLECTION_NAME"
            override val subscriptionQueryArgs = emptyMap<String, Any>()
            override val evictionQuery = ""
        }
        dittoStoreManager.registerSubscription(subscription)
    }

    private fun startTerminalsSubscription() {
        val subscription = object : DittoCollectionSubscription {
            override val collectionName = TERMINALS_COLLECTION_NAME
            override val subscriptionQuery = "SELECT * FROM $TERMINALS_COLLECTION_NAME"
            override val subscriptionQueryArgs = emptyMap<String, Any>()
            override val evictionQuery = ""
        }
        dittoStoreManager.registerSubscription(subscription)
    }

    private fun startGatewayPerformanceSubscription() {
        val subscription = object : DittoCollectionSubscription {
            override val collectionName = GATEWAY_PERFORMANCE_COLLECTION_NAME
            override val subscriptionQuery = "SELECT * FROM $GATEWAY_PERFORMANCE_COLLECTION_NAME"
            override val subscriptionQueryArgs = emptyMap<String, Any>()
            override val evictionQuery = ""
        }
        dittoStoreManager.registerSubscription(subscription)
    }

    // --- Inventory Operations ---
    fun getAvailableItems(): Flow<List<Item>> {
        return dittoStoreManager.observeLiveQueryAsFlow(GetAllItemsQuery())
    }

    suspend fun insertItem(item: Item) {
        dittoStoreManager.executeQuery(InsertItemQuery(item))
    }


    suspend fun saveOrder(order: Order) {
        dittoStoreManager.executeQuery(UpsertOrderQuery(order))
    }

    suspend fun getItemById(itemId: String): Item? {
        return dittoStoreManager.observeLiveQueryAsFlow(GetItemByIdQuery(itemId)).firstOrNull()
    }

    suspend fun deleteOrder(orderId: String) {
        dittoStoreManager.executeQuery(DeleteOrderQuery(orderId))
    }

    // --- Transaction Operations ---
    fun observeTransactions(): Flow<List<Transaction>> {
        return dittoStoreManager.observeLiveQueryAsFlow(GetAllTransactionsQuery())
    }

    suspend fun saveTransaction(transaction: Transaction) {
        dittoStoreManager.executeQuery(InsertTransactionQuery(transaction))
    }

    // --- Terminal Operations ---
    suspend fun upsertTerminal(terminal: Terminal) {
        dittoStoreManager.executeQuery(UpsertTerminalQuery(terminal))
    }

    // --- Gateway Performance Operations ---
    fun observePerformanceRankings(): Flow<List<GatewayPerformance>> {
        return dittoStoreManager.observeLiveQueryAsFlow(GetAllGatewayPerformanceQuery())
    }

    suspend fun upsertPerformance(performance: GatewayPerformance) {
        dittoStoreManager.executeQuery(UpsertGatewayPerformanceQuery(performance))
    }

    fun observeOrderById(orderId: String): Flow<Order?> {
        return dittoStoreManager.observeLiveQueryAsFlow(GetOrderByIdQuery(orderId))
    }
}