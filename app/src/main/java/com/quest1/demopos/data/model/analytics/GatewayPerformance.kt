package com.quest1.demopos.data.model.analytics

import live.ditto.ditto_wrapper.DittoProperty
import live.ditto.ditto_wrapper.deserializeProperty

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

        fun fromDittoDocument(doc: DittoProperty): GatewayPerformance {
            return GatewayPerformance(
                _id = doc.deserializeProperty("_id"),
                gatewayId = doc.deserializeProperty("gatewayId"),
                gatewayName = doc.deserializeProperty("gatewayName"),
                totalAttempts = (doc.deserializeProperty<Any>("totalAttempts") as Number).toLong(),
                totalSuccesses = (doc.deserializeProperty<Any>("totalSuccesses") as Number).toLong(),
                successRate = (doc.deserializeProperty<Any>("successRate") as Number).toDouble()
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