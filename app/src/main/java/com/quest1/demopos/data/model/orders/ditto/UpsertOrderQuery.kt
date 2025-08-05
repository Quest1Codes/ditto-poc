package com.quest1.demopos.data.model.orders.ditto

import com.quest1.demopos.data.model.orders.Order
import live.ditto.ditto_wrapper.dittowrappers.DittoQuery

class UpsertOrderQuery(private val order: Order) : DittoQuery {
    override val queryString: String = UPSERT_ORDER_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("order" to order.serializeAsMap())
}