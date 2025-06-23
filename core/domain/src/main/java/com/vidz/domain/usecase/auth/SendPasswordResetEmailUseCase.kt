package com.vidz.domain.usecase.auth

import com.vidz.domain.Result
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Flow<Result<Unit>> {
        return authRepository.sendPasswordResetEmail(email)
    }
} 