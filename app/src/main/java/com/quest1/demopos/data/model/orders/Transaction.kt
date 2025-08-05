// REFACTORED FILE
package com.quest1.demopos.data.model.orders

import android.util.Log
import live.ditto.ditto_wrapper.DittoProperty
import live.ditto.ditto_wrapper.MissingPropertyException
import live.ditto.ditto_wrapper.deserializeProperty

data class Transaction(
    val id: String,
    val orderId: String,
    val acquirerId: String,
    val acquirerName: String,
    val status: String,
    val amount: Double,
    val currency: String,
    val failureReason: String?,
    val latencyMs: Long,
    val createdAt: Long
) {
    fun serializeAsMap(): Map<String, Any?> {
        return mapOf(
            "_id" to id,
            "orderId" to orderId,
            "acquirerId" to acquirerId,
            "acquirerName" to acquirerName,
            "status" to status,
            "amount" to amount,
            "currency" to currency,
            "failureReason" to failureReason,
            "latencyMs" to latencyMs,
            "createdAt" to createdAt
        )
    }
}

fun DittoProperty.toTransaction(): Transaction {
    return Transaction(
        id = this["_id"] as? String ?: "unknown_id",
        orderId = this["orderId"] as? String ?: "unknown_order",
        acquirerId = this["acquirerId"] as? String ?: "unknown",
        acquirerName = this["acquirerName"] as? String ?: "Unknown",
        status = this["status"] as? String ?: "UNKNOWN",
        amount = (this["amount"] as? Number)?.toDouble() ?: 0.0,
        currency = this["currency"] as? String ?: "USD",
        failureReason = this["failureReason"] as? String,
        latencyMs = (this["latencyMs"] as? Number)?.toLong() ?: 0L,
        createdAt = (this["createdAt"] as? Number)?.toLong() ?: 0L
    )
}