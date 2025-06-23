package com.vidz.domain.repository

import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    
    /**
     * Get payment status for an order
     */
    suspend fun getPaymentStatus(orderId: String): Flow<Result<String>>
    
    /**
     * Handle payment success callback
     */
    suspend fun handlePaymentSuccess(orderId: String): Flow<Result<String>>
    
    /**
     * Handle payment cancellation
     */
    suspend fun handlePaymentCancel(orderId: String): Flow<Result<String>>
    
    /**
     * Handle payment webhook
     */
    suspend fun handlePaymentWebhook(payload: String): Flow<Result<String>>
} 