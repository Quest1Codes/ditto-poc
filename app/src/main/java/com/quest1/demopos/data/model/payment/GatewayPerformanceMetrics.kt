package com.quest1.demopos.data.model.payment

import java.util.UUID


data class GatewayPerformanceMetrics(
    val id: String = UUID.randomUUID().toString().substring(0, 8),
    val transactionId: String,
    val gatewayId: String,
    val terminalId: String,
    val timestamp: Long,
    val metrics: Map<String, Any>,
    val wasSuccess: Boolean,
    val failureCode: String?
)