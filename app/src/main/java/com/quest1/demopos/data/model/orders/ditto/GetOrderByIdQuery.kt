package com.quest1.demopos.data.model.orders.ditto

import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.toOrder
import live.ditto.ditto_wrapper.DittoPropertyDeserializer
import live.ditto.ditto_wrapper.dittowrappers.DittoSelectQuery

class GetOrderByIdQuery(private val orderId: String) : DittoSelectQuery<Order?> {
    override val queryString: String = GET_ORDER_BY_ID_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("orderId" to orderId)

    override val documentDeserializer: DittoPropertyDeserializer<Order?>
        get() = { docs ->
            docs.firstOrNull()?.toOrder()
        }
}