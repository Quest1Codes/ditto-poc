package com.quest1.demopos.data.model.core

data class Store(
    val id: String,
    val name: String,
    val organizationId: Int,
    val location: Location
)
