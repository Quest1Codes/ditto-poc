package com.quest1.demopos.data.model.payment

//Represents a payment gateway/acquirer.

data class Gateway(
    val id: String,
    val name: String,
    val apiEndpoint: String,
    val supportedPaymentMethod: String
)