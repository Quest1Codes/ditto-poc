package com.example.quest1pos.data.model.payment

data class GatewayPerformanceMetrics(
    val id: String,
    val transactionId: String,
    val gatewayId: String,
    val terminalId: String,
    val timestamp: Long,
    val metrics: Map<String, Any>,
    val wasSuccess: Boolean,
    val failureCode: String?
)
