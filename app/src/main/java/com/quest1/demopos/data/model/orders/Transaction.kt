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
    return try {
        Transaction(
            id = deserializeProperty("_id"),
            orderId = deserializeProperty("orderId"),
            acquirerId = deserializeProperty("acquirerId"),
            acquirerName = deserializeProperty("acquirerName"),
            status = deserializeProperty("status"),
            amount = (deserializeProperty<Number>("amount")).toDouble(),
            currency = deserializeProperty("currency"),
            failureReason = this["failureReason"] as? String,
            latencyMs = (deserializeProperty<Number>("latencyMs")).toLong(),
            createdAt = (deserializeProperty<Number>("createdAt")).toLong()
        )
    } catch (e: Exception) {
        Log.e("Transaction.kt", "Error mapping document: $this", e)
        throw MissingPropertyException("Error deserializing Transaction", this)
    }
}