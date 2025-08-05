// File: app/src/main/java/com/quest1/demopos/data/model/analytics/GatewayPerformance.kt
package com.quest1.demopos.data.model.analytics

import live.ditto.ditto_wrapper.DittoProperty

/**
 * Represents the performance metrics for a single payment gateway.
 * The _id is the gateway's unique ID to ensure one document per gateway.
 */
data class GatewayPerformance(
    val _id: String,
    val gatewayId: String,
    val gatewayName: String,
    val totalAttempts: Long,
    val totalSuccesses: Long,
    val successRate: Double
) {
    companion object {
        const val COLLECTION_NAME = "gateway_performance"

        /**
         * MODIFIED: This function now safely handles null or missing properties
         * by providing default values, preventing the app from crashing.
         */
        fun fromDittoDocument(doc: DittoProperty): GatewayPerformance {
            return GatewayPerformance(
                _id = doc["_id"] as? String ?: "unknown_id",
                gatewayId = doc["gatewayId"] as? String ?: "",
                gatewayName = doc["gatewayName"] as? String ?: "Unknown Gateway",
                totalAttempts = (doc["totalAttempts"] as? Number)?.toLong() ?: 0L,
                totalSuccesses = (doc["totalSuccesses"] as? Number)?.toLong() ?: 0L,
                successRate = (doc["successRate"] as? Number)?.toDouble() ?: 0.0
            )
        }
    }
}

fun GatewayPerformance.toDocument(): Map<String, Any?> {
    return mapOf(
        "_id" to this._id,
        "gatewayId" to this.gatewayId,
        "gatewayName" to this.gatewayName,
        "totalAttempts" to this.totalAttempts,
        "totalSuccesses" to this.totalSuccesses,
        "successRate" to this.successRate
    )
}