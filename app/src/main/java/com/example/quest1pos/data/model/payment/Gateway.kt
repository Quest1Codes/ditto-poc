package com.example.quest1pos.data.model.payment

data class Gateway(
    val id: String,
    val name: String,
    val apiEndpoints: Map<String, String>,
    val supportedPaymentMethods: List<String>
)
