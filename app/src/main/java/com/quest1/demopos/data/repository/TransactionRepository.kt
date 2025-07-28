package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.orders.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    companion object {
        // The query is now co-located with the repository that uses it.
        const val INSERT_TRANSACTION_QUERY = """
            INSERT INTO ${Transaction.COLLECTION_NAME}
            DOCUMENTS (:transaction)
            ON ID CONFLICT DO UPDATE
        """
    }

    init {
        // Start a subscription to sync all transaction data from the Ditto mesh.
        val subscriptionQuery = "SELECT * FROM ${Transaction.COLLECTION_NAME}"
        dittoRepository.startSubscription(subscriptionQuery)
    }

    /**
     * Inserts or updates a Transaction in the database.
     */
    suspend fun saveTransaction(transaction: Transaction) {
        // Convert the Transaction object into a Map that Ditto can store.
        val transactionMap = mapOf(
            "_id" to transaction.id,
            "orderId" to transaction.orderId,
            "acquirerId" to transaction.acquirerId,
            "acquirerName" to transaction.acquirerName,
            "status" to transaction.status,
            "amount" to transaction.amount,
            "currency" to transaction.currency,
            "failureReason" to transaction.failureReason,
            "latencyMs" to transaction.latencyMs,
            "createdAt" to transaction.createdAt
        )
        dittoRepository.upsert(Transaction.COLLECTION_NAME, transactionMap)
    }
}
