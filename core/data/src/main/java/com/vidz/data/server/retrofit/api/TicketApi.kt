package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.PageDto
import com.vidz.data.server.dto.TicketDto
import com.vidz.data.server.dto.TicketUpsertRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketApi {
    
    @GET("tickets")
    suspend fun getTickets(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null
    ): PageDto<TicketDto>
    
    @GET("tickets/{id}")
    suspend fun getTicketById(
        @Path("id") id: String
    ): TicketDto
    
    @GET("tickets/number/{ticketNumber}")
    suspend fun getTicketByNumber(
        @Path("ticketNumber") ticketNumber: String
    ): TicketDto
    
    @GET("tickets/status/{status}")
    suspend fun getTicketsByStatus(
        @Path("status") status: String
    ): List<TicketDto>
    
    @GET("tickets/order-detail/{orderDetailId}")
    suspend fun getTicketsByOrderDetailId(
        @Path("orderDetailId") orderDetailId: String
    ): List<TicketDto>
    
    @POST("tickets")
    suspend fun createTicket(
        @Body request: TicketUpsertRequestDto
    ): TicketDto
    
    @POST("tickets/batch")
    suspend fun createTickets(
        @Body requests: List<TicketUpsertRequestDto>
    ): List<TicketDto>
    
    @PUT("tickets/{id}/status")
    suspend fun updateTicketStatus(
        @Path("id") id: String,
        @Query("status") status: String
    )

    @GET("tickets/{id}/qrcode")
    suspend fun getTicketQRCode(
        @Path("id") ticketId: String
    ): Response<okhttp3.ResponseBody>
} 
