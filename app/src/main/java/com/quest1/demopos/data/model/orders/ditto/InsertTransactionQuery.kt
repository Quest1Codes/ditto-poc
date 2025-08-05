package com.quest1.demopos.data.model.orders.ditto

import com.quest1.demopos.data.model.orders.Transaction
import live.ditto.ditto_wrapper.dittowrappers.DittoQuery

class InsertTransactionQuery(private val transaction: Transaction) : DittoQuery {
    override val queryString: String = INSERT_TRANSACTION_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("transaction" to transaction.serializeAsMap())
}