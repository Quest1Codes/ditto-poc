package com.quest1.demopos.data.model.analytics.ditto

import com.quest1.demopos.data.model.analytics.GatewayPerformance
import live.ditto.ditto_wrapper.dittowrappers.DittoQuery

class UpsertGatewayPerformanceQuery(
    private val performance: GatewayPerformance
) : DittoQuery {
    override val queryString: String = UPSERT_GATEWAY_PERFORMANCE_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("performance" to performance.serializeAsMap())
}