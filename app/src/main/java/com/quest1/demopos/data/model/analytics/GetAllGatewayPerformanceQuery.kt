package com.quest1.demopos.data.model.analytics

import com.quest1.demopos.data.model.analytics.GatewayPerformance
import live.ditto.ditto_wrapper.DittoPropertyDeserializer
import live.ditto.ditto_wrapper.dittowrappers.DittoSelectQuery

class GetAllGatewayPerformanceQuery : DittoSelectQuery<List<GatewayPerformance>> {
    override val queryString: String
        get() = "SELECT * FROM ${GatewayPerformance.COLLECTION_NAME} ORDER BY successRate DESC"

    override val documentDeserializer: DittoPropertyDeserializer<List<GatewayPerformance>>
        get() = { docs ->
            docs.map { GatewayPerformance.fromDittoDocument(it) }
        }
}