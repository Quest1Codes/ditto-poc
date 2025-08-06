package com.quest1.demopos.domain.usecase.order

import com.quest1.demopos.data.model.orders.Order
import com.quest1.demopos.data.repository.DittoRepository
import com.quest1.demopos.data.repository.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetActiveOrderUseCase @Inject constructor(
    private val dittoRepository: DittoRepository,
    private val sessionManager: SessionManager
) {
    fun execute(): Flow<Order?> {
        return sessionManager.activeOrderId.flatMapLatest { orderId ->
            if (orderId == null) {
                flowOf(null)
            } else {
                dittoRepository.observeOrderById(orderId)
            }
        }
    }
}