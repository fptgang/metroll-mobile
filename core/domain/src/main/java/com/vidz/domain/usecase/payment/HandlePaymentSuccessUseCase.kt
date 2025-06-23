package com.vidz.domain.usecase.payment

import com.vidz.domain.Result
import com.vidz.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HandlePaymentSuccessUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    
    suspend operator fun invoke(orderId: String): Flow<Result<String>> {
        return paymentRepository.handlePaymentSuccess(orderId)
    }
} 