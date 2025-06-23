package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.PageDto
import com.vidz.data.server.dto.TimedTicketPlanCreateRequestDto
import com.vidz.data.server.dto.TimedTicketPlanDto
import com.vidz.data.server.dto.TimedTicketPlanUpdateRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TimedTicketPlanApi {
    
    @GET("timed-ticket-plans/")
    suspend fun getTimedTicketPlans(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null
    ): PageDto<TimedTicketPlanDto>
    
    @GET("timed-ticket-plans/{id}")
    suspend fun getTimedTicketPlanById(
        @Path("id") id: String
    ): TimedTicketPlanDto
    
    @POST("timed-ticket-plans/")
    suspend fun createTimedTicketPlan(
        @Body request: TimedTicketPlanCreateRequestDto
    ): TimedTicketPlanDto
    
    @PUT("timed-ticket-plans/{id}")
    suspend fun updateTimedTicketPlan(
        @Path("id") id: String,
        @Body request: TimedTicketPlanUpdateRequestDto
    ): TimedTicketPlanDto
    
    @DELETE("timed-ticket-plans/{id}")
    suspend fun deleteTimedTicketPlan(
        @Path("id") id: String
    )
} 
