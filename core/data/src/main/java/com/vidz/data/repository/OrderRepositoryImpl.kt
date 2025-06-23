package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.OrderMapper
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.CheckoutRequest
import com.vidz.domain.model.Order
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override suspend fun checkout(request: CheckoutRequest): Flow<Result<Order>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.orderApi.checkout(orderMapper.checkoutRequestToDto(request))
                response.body() ?: throw NullPointerException("Checkout response body is null")
            },
            convert = { orderDto ->
                orderMapper.toDomain(orderDto)
            }
        ).execute()
    }

    override suspend fun getAllOrders(
        page: Int?,
        size: Int?,
        search: String?
    ): Flow<Result<PageDto<Order>>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.orderApi.getAllOrders(page, size, search)
                response.body() ?: throw NullPointerException("Get all orders response body is null")
            },
            convert = { pageDto ->
                PageDto(
                    content = orderMapper.toDomainList(pageDto.content),
                    pageNumber = pageDto.pageNumber,
                    pageSize = pageDto.pageSize,
                    totalElements = pageDto.totalElements,
                    totalPages = pageDto.totalPages,
                    last = pageDto.last
                )
            }
        ).execute()
    }

    override suspend fun getOrderById(orderId: String): Flow<Result<Order>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.orderApi.getOrderById(orderId)
                response.body() ?: throw NullPointerException("Get order by ID response body is null")
            },
            convert = { orderDto ->
                orderMapper.toDomain(orderDto)
            }
        ).execute()
    }

    override suspend fun getMyOrders(
        page: Int?,
        size: Int?,
        search: String?
    ): Flow<Result<PageDto<Order>>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.orderApi.getMyOrders(page, size, search)
                response.body() ?: throw NullPointerException("Get my orders response body is null")
            },
            convert = { pageDto ->
                PageDto(
                    content = orderMapper.toDomainList(pageDto.content),
                    pageNumber = pageDto.pageNumber,
                    pageSize = pageDto.pageSize,
                    totalElements = pageDto.totalElements,
                    totalPages = pageDto.totalPages,
                    last = pageDto.last
                )
            }
        ).execute()
    }
} 