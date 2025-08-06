package com.quest1.demopos.data.model.orders.ditto

const val TRANSACTIONS_COLLECTION_NAME = "transactions"

const val GET_ALL_TRANSACTIONS_QUERY = """
    SELECT * FROM $TRANSACTIONS_COLLECTION_NAME ORDER BY createdAt DESC
"""

const val INSERT_TRANSACTION_QUERY = """
    INSERT INTO $TRANSACTIONS_COLLECTION_NAME
    DOCUMENTS (:transaction)
    ON ID CONFLICT DO UPDATE
"""