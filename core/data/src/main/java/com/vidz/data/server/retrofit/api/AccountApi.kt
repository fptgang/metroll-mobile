package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.AccountDto
import com.vidz.data.server.retrofit.dto.GetAccounts200Response
import com.vidz.domain.model.AccountUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountApi {

    @POST("accounts")
    suspend fun createAccount(
        @Body accountDto: AccountDto
    ): Response<AccountDto>

    @GET("accounts/{accountId}")
    suspend fun getAccountById(
        @Path("accountId") accountId: String
    ): Response<AccountDto>

    @GET("accounts")
    suspend fun getAccounts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: List<String>? = null,
        @Query("filter") filter: String? = null,
        @Query("search") search: String? = null
    ): Response<GetAccounts200Response>

    @PUT("accounts/{accountId}")
    suspend fun updateAccount(
        @Path("accountId") accountId: String,
        @Body accountDto: AccountUpdateRequest
    ): Response<AccountDto>

    @DELETE("accounts/{accountId}")
    suspend fun deleteAccount(
        @Path("accountId") accountId: String
    ): Response<Unit>

    @GET("account-discount-packages/my-discount-percentage")
    suspend fun getMyDiscountPercentage(): Response<Float?>
} 
