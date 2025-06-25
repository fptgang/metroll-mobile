package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.PageDto
import com.vidz.data.server.dto.StationDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StationApi {
    
    @GET("/stations")
    suspend fun getStations(
        @Query("name") name: String? = null,
        @Query("code") code: String? = null,
        @Query("status") status: String? = null,
        @Query("lineCode") lineCode: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): PageDto<StationDto>
    
    @GET("/stations/{code}")
    suspend fun getStationByCode(
        @Path("code") code: String
    ): StationDto
    
    @POST("/stations")
    suspend fun createStation(
        @Body station: StationDto
    ): StationDto
    
    @POST("/stations/create-list")
    suspend fun createStationList(
        @Body stations: List<StationDto>
    ): List<StationDto>
} 