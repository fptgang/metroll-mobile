package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.PageDto
import com.vidz.data.server.dto.TicketDto
import com.vidz.data.server.dto.TicketUpsertRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketApi {
    
    @GET("/")
    suspend fun getTickets(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null
    ): PageDto<TicketDto>
    
    @GET("/{id}")
    suspend fun getTicketById(
        @Path("id") id: String
    ): TicketDto
    
    @GET("/number/{ticketNumber}")
    suspend fun getTicketByNumber(
        @Path("ticketNumber") ticketNumber: String
    ): TicketDto
    
    @GET("/status/{status}")
    suspend fun getTicketsByStatus(
        @Path("status") status: String
    ): List<TicketDto>
    
    @GET("/order-detail/{orderDetailId}")
    suspend fun getTicketsByOrderDetailId(
        @Path("orderDetailId") orderDetailId: String
    ): List<TicketDto>
    
    @POST("/")
    suspend fun createTicket(
        @Body request: TicketUpsertRequestDto
    ): TicketDto
    
    @POST("/batch")
    suspend fun createTickets(
        @Body requests: List<TicketUpsertRequestDto>
    ): List<TicketDto>
    
    @PUT("/{id}/status")
    suspend fun updateTicketStatus(
        @Path("id") id: String,
        @Query("status") status: String
    )
} 
