package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.analytics.GatewayPerformance
import com.quest1.demopos.data.model.analytics.ditto.GATEWAY_PERFORMANCE_COLLECTION_NAME
import com.quest1.demopos.data.model.analytics.ditto.GetAllGatewayPerformanceQuery
import kotlinx.coroutines.flow.Flow
import live.ditto.ditto_wrapper.DittoStoreManager
import live.ditto.ditto_wrapper.dittowrappers.DittoCollectionSubscription

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GatewayPerformanceRepository @Inject constructor(
    private val dittoStoreManager: DittoStoreManager
) {

    init {
        val subscription = object : DittoCollectionSubscription {
            override val collectionName: String = GATEWAY_PERFORMANCE_COLLECTION_NAME
            override val subscriptionQuery: String = "SELECT * FROM ${GATEWAY_PERFORMANCE_COLLECTION_NAME}"
            override val subscriptionQueryArgs: Map<String, Any> = emptyMap()
            override val evictionQuery: String = ""
        }
        dittoStoreManager.registerSubscription(subscription)
    }

    fun observePerformanceRankings(): Flow<List<GatewayPerformance>> {
        return dittoStoreManager.observeLiveQueryAsFlow(
            dittoQuery = GetAllGatewayPerformanceQuery()
        )
    }
}