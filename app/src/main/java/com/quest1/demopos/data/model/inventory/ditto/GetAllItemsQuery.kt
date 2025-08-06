package com.quest1.demopos.data.model.inventory.ditto


import com.quest1.demopos.data.model.inventory.Item
import com.quest1.demopos.data.model.inventory.toItem
import live.ditto.ditto_wrapper.DittoPropertyDeserializer
import live.ditto.ditto_wrapper.dittowrappers.DittoSelectQuery

class GetAllItemsQuery : DittoSelectQuery<List<Item>> {
    override val queryString: String = GET_ALL_ITEMS_QUERY

    override val documentDeserializer: DittoPropertyDeserializer<List<Item>>
        get() = { docs ->
            docs.map { it.toItem() }
        }
}