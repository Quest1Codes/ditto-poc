package com.quest1.demopos.data.model.orders.ditto

const val ORDERS_COLLECTION_NAME = "orders"

const val GET_ACTIVE_ORDER_QUERY = """
    SELECT * FROM $ORDERS_COLLECTION_NAME WHERE terminalId = :terminalId
"""

const val GET_ORDER_BY_ID_QUERY = """
    SELECT * FROM $ORDERS_COLLECTION_NAME WHERE _id = :orderId
"""

const val UPSERT_ORDER_QUERY = """
    INSERT INTO $ORDERS_COLLECTION_NAME
    DOCUMENTS (:order)
    ON ID CONFLICT DO UPDATE
"""

const val DELETE_ORDER_QUERY = """
    DELETE FROM $ORDERS_COLLECTION_NAME WHERE _id = :orderId
"""