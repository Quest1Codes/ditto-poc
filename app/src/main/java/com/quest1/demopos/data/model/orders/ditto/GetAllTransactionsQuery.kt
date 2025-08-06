package com.quest1.demopos.data.model.orders.ditto

import com.quest1.demopos.data.model.orders.Transaction
import com.quest1.demopos.data.model.orders.toTransaction
import live.ditto.ditto_wrapper.DittoPropertyDeserializer
import live.ditto.ditto_wrapper.dittowrappers.DittoSelectQuery

class GetAllTransactionsQuery : DittoSelectQuery<List<Transaction>> {
    override val queryString: String = GET_ALL_TRANSACTIONS_QUERY

    override val documentDeserializer: DittoPropertyDeserializer<List<Transaction>>
        get() = { docs ->
            docs.mapNotNull { it.toTransaction() }
        }
}