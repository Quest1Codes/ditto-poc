package com.quest1.demopos.data.model.inventory.ditto

import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.model.inventory.toItem
import live.ditto.ditto_wrapper.DittoPropertyDeserializer
import live.ditto.ditto_wrapper.dittowrappers.DittoSelectQuery

class GetItemByIdQuery(private val itemId: String) : DittoSelectQuery<Item?> {
    override val queryString: String = GET_ITEM_BY_ID_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("itemId" to itemId)

    override val documentDeserializer: DittoPropertyDeserializer<Item?>
        get() = { docs ->
            docs.firstOrNull()?.toItem()
        }
}