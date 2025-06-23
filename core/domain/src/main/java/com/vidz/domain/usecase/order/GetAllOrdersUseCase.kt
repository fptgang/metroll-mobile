package com.vidz.domain.usecase.order

import com.vidz.domain.Result
import com.vidz.domain.model.Order
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    
    suspend operator fun invoke(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Flow<Result<PageDto<Order>>> {
        return orderRepository.getAllOrders(page, size, search)
    }
} 