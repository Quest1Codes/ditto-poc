package com.quest1.demopos.data.model.core.ditto

const val TERMINALS_COLLECTION_NAME = "terminals"

const val UPSERT_TERMINAL_QUERY = """
    INSERT INTO $TERMINALS_COLLECTION_NAME
    DOCUMENTS (:terminal)
    ON ID CONFLICT DO UPDATE
"""