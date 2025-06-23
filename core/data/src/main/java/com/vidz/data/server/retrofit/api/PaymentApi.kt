package com.vidz.data.server.retrofit.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PaymentApi {

    @GET("payment/status/{orderId}")
    suspend fun getPaymentStatus(
        @Path("orderId") orderId: String
    ): Response<String>

    @GET("payment/success")
    suspend fun handlePaymentSuccess(
        @Query("orderId") orderId: String
    ): Response<String>

    @GET("payment/cancel")
    suspend fun handlePaymentCancel(
        @Query("orderId") orderId: String
    ): Response<String>

    @POST("payment/webhook")
    suspend fun handlePaymentWebhook(
        @Body payload: String
    ): Response<String>
} 