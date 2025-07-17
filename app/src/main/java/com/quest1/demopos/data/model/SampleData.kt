package com.quest1.demopos.data.model
data class SampleData(
    val id: String,
    val name: String,
    val price: Double? = 0.0 // FIX: Add the price property
)