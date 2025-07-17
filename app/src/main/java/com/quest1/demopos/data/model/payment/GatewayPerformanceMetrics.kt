package com.quest1.demopos.data.model.payment

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
