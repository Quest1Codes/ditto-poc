package com.example.quest1pos.data.model.core

data class Terminal(
    val id: String,
    val storeId: String,
    val name: String,
    val ipAddress: String,
    val lastSeen: Long
)
