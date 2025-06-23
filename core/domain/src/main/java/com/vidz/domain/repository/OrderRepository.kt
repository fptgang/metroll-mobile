package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.CheckoutRequest
import com.vidz.domain.model.Order
import com.vidz.domain.model.PageDto
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    
    /**
     * MSS-11: Check out - Create order and process payment
     */
    suspend fun checkout(request: CheckoutRequest): Flow<Result<Order>>
    
    /**
     * MSS-14: Read all orders - Get all orders (Admin/Staff only)
     */
    suspend fun getAllOrders(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Flow<Result<PageDto<Order>>>
    
    /**
     * MSS-15: Read one order - Get order details by ID
     */
    suspend fun getOrderById(orderId: String): Flow<Result<Order>>
    
    /**
     * MSS-13: List my orders - Get current user's orders
     */
    suspend fun getMyOrders(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Flow<Result<PageDto<Order>>>
} 