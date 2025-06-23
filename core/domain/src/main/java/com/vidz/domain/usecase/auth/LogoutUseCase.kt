package com.vidz.domain.usecase.auth

import com.vidz.domain.Result
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Result<Unit>> = flow {
        // First clear user data from datastore
        userRepository.clearUser()
        
        // Then logout from Firebase/backend
        authRepository.logout().collect { result ->
            emit(result)
        }
    }
} 