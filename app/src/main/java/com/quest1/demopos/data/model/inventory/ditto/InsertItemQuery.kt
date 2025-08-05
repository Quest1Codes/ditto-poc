package com.quest1.demopos.data.model.inventory.ditto


import com.quest1.demopos.data.model.inventory.Item
import live.ditto.ditto_wrapper.dittowrappers.DittoQuery

class InsertItemQuery(private val item: Item) : DittoQuery {
    override val queryString: String = INSERT_ITEM_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("item" to item.serializeAsMap())
}