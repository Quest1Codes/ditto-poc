package com.quest1.demopos.data.model.orders.ditto

import live.ditto.ditto_wrapper.dittowrappers.DittoQuery

class DeleteOrderQuery(private val orderId: String) : DittoQuery {
    override val queryString: String = DELETE_ORDER_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("orderId" to orderId)
}