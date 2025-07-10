package com.vidz.data.server.retrofit.api

import com.vidz.data.server.dto.PageDto
import com.vidz.data.server.dto.VoucherCreateRequestDto
import com.vidz.data.server.dto.VoucherDto
import com.vidz.data.server.dto.VoucherUpdateRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VoucherApi {

    @GET("vouchers/{id}")
    suspend fun getVoucherById(
        @Path("id") id: String
    ): Response<VoucherDto>

    @GET("vouchers")
    suspend fun listVouchers(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("userId") userId: String? = null
    ): Response<PageDto<VoucherDto>>

    @GET("vouchers/my-vouchers")
    suspend fun getMyVouchers(): Response<List<VoucherDto>>

    @POST("vouchers")
    suspend fun createVoucher(
        @Body request: VoucherCreateRequestDto
    ): Response<List<VoucherDto>>

    @PUT("vouchers/{id}")
    suspend fun updateVoucher(
        @Path("id") id: String,
        @Body request: VoucherUpdateRequestDto
    ): Response<VoucherDto>

    @DELETE("vouchers/{id}/revoke")
    suspend fun revokeVoucher(
        @Path("id") id: String
    ): Response<Unit>
} 