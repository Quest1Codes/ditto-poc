package com.quest1.demopos.data.model.analytics


import com.quest1.demopos.data.model.analytics.GatewayPerformance
import live.ditto.ditto_wrapper.dittowrappers.DittoQuery

class UpsertGatewayPerformanceQuery(
    private val performance: GatewayPerformance
): DittoQuery {
    override val queryString: String
        get() = """
            INSERT INTO ${GatewayPerformance.COLLECTION_NAME}
            DOCUMENTS (:performance)
            ON ID CONFLICT DO UPDATE
        """.trimIndent()

    override val arguments: Map<String, Any>
        get() = mapOf("performance" to performance.toDocument())
}