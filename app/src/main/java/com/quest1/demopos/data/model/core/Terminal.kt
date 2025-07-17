package com.quest1.demopos.data.model.core

data class Terminal(
    val id: String,
    val storeId: String,
    val name: String,
    val ipAddress: String,
    val lastSeen: Long
)
