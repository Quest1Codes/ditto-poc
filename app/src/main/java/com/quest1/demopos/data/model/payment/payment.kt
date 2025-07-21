package com.quest1.demopos.data.model.payment

data class PaymentCard(
    val id: String,
    val transactionId: String,
    val acquirerName: String,
    val amount: String,
    val currency: String,
    val date: String,
    val time: String,
    val location: String,
    val status: String, // "SUCCESS", "FAILED", "PENDING"
    val gatewayId: String
)

data class PaymentLocation(
    val storeName: String,
    val address: String,
    val city: String
)
