package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.PageDto
import com.vidz.data.server.dto.TicketValidationCreateRequestDto
import com.vidz.data.server.dto.TicketValidationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketValidationApi {
    
    @GET("ticket-validations")
    suspend fun getTicketValidations(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null
    ): PageDto<TicketValidationDto>
    
    @GET("ticket-validations/{id}")
    suspend fun getTicketValidationById(
        @Path("id") id: String
    ): TicketValidationDto
    
    @GET("ticket-validations/ticket/{ticketId}")
    suspend fun getTicketValidationsByTicketId(
        @Path("ticketId") ticketId: String
    ): List<TicketValidationDto>
    
    @GET("ticket-validations/station/{stationCode}")
    suspend fun getTicketValidationsByStationCode(
        @Path("stationCode") stationCode: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null,
        @Query("validationType") validationType: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): PageDto<TicketValidationDto>
    
    @POST("ticket-validations/validate")
    suspend fun validateTicket(
        @Body request: TicketValidationCreateRequestDto
    ): TicketValidationDto
} 
