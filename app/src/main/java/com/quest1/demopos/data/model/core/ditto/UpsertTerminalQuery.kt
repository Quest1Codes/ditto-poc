package com.quest1.demopos.data.model.core.ditto

import com.quest1.demopos.data.model.core.Terminal
import live.ditto.ditto_wrapper.dittowrappers.DittoQuery

class UpsertTerminalQuery(private val terminal: Terminal) : DittoQuery {
    override val queryString: String = UPSERT_TERMINAL_QUERY

    override val arguments: Map<String, Any>
        get() = mapOf("terminal" to terminal.serializeAsMap())
}