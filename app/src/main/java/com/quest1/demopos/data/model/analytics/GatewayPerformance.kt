package com.quest1.demopos.data.model.analytics

import live.ditto.ditto_wrapper.DittoProperty
import live.ditto.ditto_wrapper.MissingPropertyException
import live.ditto.ditto_wrapper.deserializeProperty

data class GatewayPerformance(
    val _id: String,
    val gatewayId: String,
    val gatewayName: String,
    val totalAttempts: Long,
    val totalSuccesses: Long,
    val successRate: Double
) {
    fun serializeAsMap(): Map<String, Any?> {
        return mapOf(
            "_id" to this._id,
            "gatewayId" to this.gatewayId,
            "gatewayName" to this.gatewayName,
            "totalAttempts" to this.totalAttempts,
            "totalSuccesses" to this.totalSuccesses,
            "successRate" to this.successRate
        )
    }
}

fun DittoProperty.toGatewayPerformance(): GatewayPerformance {
    return try {
        GatewayPerformance(
            _id = deserializeProperty("_id"),
            gatewayId = deserializeProperty("gatewayId"),
            gatewayName = deserializeProperty("gatewayName"),
            totalAttempts = (deserializeProperty<Number>("totalAttempts")).toLong(),
            totalSuccesses = (deserializeProperty<Number>("totalSuccesses")).toLong(),
            successRate = (deserializeProperty<Number>("successRate")).toDouble()
        )
    } catch(e: Exception) {
        throw MissingPropertyException("Error deserializing GatewayPerformance", this)
    }
}