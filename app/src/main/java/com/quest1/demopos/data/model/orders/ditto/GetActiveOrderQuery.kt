package com.quest1.demopos.data.model.orders.ditto

import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.model.orders.toOrder
import live.ditto.ditto_wrapper.DittoPropertyDeserializer
import live.ditto.ditto_wrapper.dittowrappers.DittoSelectQuery

class GetActiveOrderQuery(private val terminalId: String) : DittoSelectQuery<Order?> {
    override val queryString: String = GET_ACTIVE_ORDER_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("terminalId" to terminalId)

    override val documentDeserializer: DittoPropertyDeserializer<Order?>
        get() = { docs ->
            docs.firstOrNull()?.toOrder()
        }
}