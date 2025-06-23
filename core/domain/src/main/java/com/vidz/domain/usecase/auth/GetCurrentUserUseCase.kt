package com.vidz.domain.usecase.auth

import com.vidz.domain.Result
import com.vidz.domain.model.User
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<Result<User?>> {
        return authRepository.getCurrentUser()
    }
} 