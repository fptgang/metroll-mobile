package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.MetroLineDto
import com.vidz.data.server.dto.MetroLineRequestDto
import com.vidz.data.server.dto.PageDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MetroLineApi {
    
    @GET("/lines")
    suspend fun getMetroLines(
        @Query("name") name: String? = null,
        @Query("code") code: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): PageDto<MetroLineDto>
    
    @GET("/lines/{code}")
    suspend fun getMetroLineByCode(
        @Path("code") code: String
    ): MetroLineDto
    
    @POST("/lines")
    suspend fun createMetroLine(
        @Body request: MetroLineRequestDto
    ): MetroLineDto
    
    @PUT("/lines/{code}")
    suspend fun updateMetroLine(
        @Path("code") code: String,
        @Body request: MetroLineRequestDto
    ): MetroLineDto
}