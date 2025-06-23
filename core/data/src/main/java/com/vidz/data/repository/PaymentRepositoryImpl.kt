package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer
) : PaymentRepository {

    override suspend fun getPaymentStatus(orderId: String): Flow<Result<String>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.paymentApi.getPaymentStatus(orderId)
                response.body() ?: throw NullPointerException("Payment status response body is null")
            },
            convert = { paymentStatus ->
                paymentStatus
            }
        ).execute()
    }

    override suspend fun handlePaymentSuccess(orderId: String): Flow<Result<String>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.paymentApi.handlePaymentSuccess(orderId)
                response.body() ?: throw NullPointerException("Payment success response body is null")
            },
            convert = { result ->
                result
            }
        ).execute()
    }

    override suspend fun handlePaymentCancel(orderId: String): Flow<Result<String>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.paymentApi.handlePaymentCancel(orderId)
                response.body() ?: throw NullPointerException("Payment cancel response body is null")
            },
            convert = { result ->
                result
            }
        ).execute()
    }

    override suspend fun handlePaymentWebhook(payload: String): Flow<Result<String>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.paymentApi.handlePaymentWebhook(payload)
                response.body() ?: throw NullPointerException("Payment webhook response body is null")
            },
            convert = { result ->
                result
            }
        ).execute()
    }
} 