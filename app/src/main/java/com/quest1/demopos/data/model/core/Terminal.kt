package com.quest1.demopos.data.model.core

data class Terminal(
    val _id: String,
    val storeId: String,
    val name: String,
    val ipAddress: String,
    val lastSeen: Long
) {
    fun toDocument(): Map<String, Any?> {
        return mapOf(
            "_id" to _id,
            "storeId" to storeId,
            "name" to name,
            "ipAddress" to ipAddress,
            "lastSeen" to lastSeen
        )
    }

    companion object {
        const val COLLECTION_NAME = "terminals"
    }
}