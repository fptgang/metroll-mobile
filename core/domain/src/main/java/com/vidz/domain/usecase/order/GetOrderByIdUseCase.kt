package com.vidz.domain.usecase.order

import com.vidz.domain.Result
import com.vidz.domain.model.Order
import com.vidz.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    
    suspend operator fun invoke(orderId: String): Flow<Result<Order>> {
        return orderRepository.getOrderById(orderId)
    }
} 