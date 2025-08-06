package com.quest1.demopos.data.model.analytics.ditto

import com.quest1.demopos.data.model.analytics.GatewayPerformance
import com.quest1.demopos.data.model.analytics.toGatewayPerformance
import live.ditto.ditto_wrapper.DittoPropertyDeserializer
import live.ditto.ditto_wrapper.dittowrappers.DittoSelectQuery

class GetAllGatewayPerformanceQuery : DittoSelectQuery<List<GatewayPerformance>> {
    override val queryString: String = GET_ALL_GATEWAY_PERFORMANCE_QUERY

    override val documentDeserializer: DittoPropertyDeserializer<List<GatewayPerformance>>
        get() = { docs ->
            docs.map { it.toGatewayPerformance() }
        }
}