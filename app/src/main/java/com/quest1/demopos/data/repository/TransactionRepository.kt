package com.quest1.demopos.data.repository

import android.util.Log
import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    companion object {
        const val TAG = "TransactionRepository"
        const val INSERT_TRANSACTION_QUERY = """
            INSERT INTO ${Transaction.COLLECTION_NAME}
            DOCUMENTS (:transaction)
            ON ID CONFLICT DO UPDATE
        """
    }

    init {
        val subscriptionQuery = "SELECT * FROM ${Transaction.COLLECTION_NAME}"
        dittoRepository.startSubscription(subscriptionQuery)
    }

    suspend fun saveTransaction(transaction: Transaction) {
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

    fun observeTransactions(): Flow<List<Transaction>> {
        val query = "SELECT * FROM ${Transaction.COLLECTION_NAME} ORDER BY createdAt DESC"
        val arguments = mapOf("status" to Order.STATUS_PENDING)

        return dittoRepository.observeCollection(query, arguments).map { documents ->
            documents.mapNotNull{ docMap ->
                try {
                    Transaction(
                        id = docMap["_id"].toString(),
                        orderId = docMap["orderId"] as String,
                        acquirerId = docMap["acquirerId"] as String,
                        acquirerName = docMap["acquirerName"] as String,
                        status = docMap["status"] as String,
                        amount = docMap["amount"].toString().toDouble(),
                        currency = docMap["currency"] as String,
                        failureReason = (docMap["failureReason"] ?: "") as String,
                        latencyMs = docMap["latencyMs"].toString().toLong(),
                        createdAt = docMap["createdAt"].toString().toLong(),

                        )
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping order document: $docMap", e)
                    null
                }
            }
        }
    }
}