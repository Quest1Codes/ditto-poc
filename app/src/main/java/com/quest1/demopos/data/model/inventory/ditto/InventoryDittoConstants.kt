package com.quest1.demopos.data.model.inventory.ditto

const val ITEMS_COLLECTION_NAME = "items"

const val GET_ALL_ITEMS_QUERY = """
    SELECT * FROM $ITEMS_COLLECTION_NAME
"""
const val GET_ITEM_BY_ID_QUERY = """
    SELECT * FROM $ITEMS_COLLECTION_NAME WHERE _id = :itemId
"""

const val INSERT_ITEM_QUERY = """
    INSERT INTO $ITEMS_COLLECTION_NAME
    DOCUMENTS (:item)
    ON ID CONFLICT DO UPDATE
"""