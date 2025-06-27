package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.P2PJourneyCreateRequestDto
import com.vidz.data.server.dto.P2PJourneyDto
import com.vidz.data.server.dto.P2PJourneyUpdateRequestDto
import com.vidz.data.server.dto.PageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface P2PJourneyApi {
    
    @GET("p2p-journeys")
    suspend fun getP2PJourneys(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("search") search: String? = null
    ): PageDto<P2PJourneyDto>
    
    @GET("p2p-journeys/{id}")
    suspend fun getP2PJourneyById(
        @Path("id") id: String
    ): P2PJourneyDto
    
    @GET("p2p-journeys/stations")
    suspend fun getP2PJourneyByStations(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("startStationId") startStationId: String,
        @Query("endStationId") endStationId: String
    ): PageDto<P2PJourneyDto>
    
    @POST("p2p-journeys")
    suspend fun createP2PJourney(
        @Body request: P2PJourneyCreateRequestDto
    ): P2PJourneyDto
    
    @PUT("p2p-journeys/{id}")
    suspend fun updateP2PJourney(
        @Path("id") id: String,
        @Body request: P2PJourneyUpdateRequestDto
    ): P2PJourneyDto
    
    @DELETE("p2p-journeys/{id}")
    suspend fun deleteP2PJourney(
        @Path("id") id: String
    )
} 
