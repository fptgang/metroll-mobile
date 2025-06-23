package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.CheckoutRequestDto
import com.vidz.data.server.dto.OrderDto
import com.vidz.data.server.dto.PageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {

    @POST("checkout")
    suspend fun checkout(
        @Body request: CheckoutRequestDto
    ): Response<OrderDto>

    @GET("orders")
    suspend fun getAllOrders(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null
    ): Response<PageDto<OrderDto>>

    @GET("orders/{orderId}")
    suspend fun getOrderById(
        @Path("orderId") orderId: String
    ): Response<OrderDto>

    @GET("my-orders")
    suspend fun getMyOrders(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null
    ): Response<PageDto<OrderDto>>
} 