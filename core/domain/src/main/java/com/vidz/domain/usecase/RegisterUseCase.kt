package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.LoginValidation
import com.vidz.domain.model.RegisterRequest
import com.vidz.domain.model.User
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Flow<Result<User>> = flow {
        try {
            emit(Result.Init)
            
            // Validate registration data
            val validation = authRepository.validateRegistrationData(request)
            if (validation is LoginValidation.Invalid) {
                emit(Result.ServerError.MissingParam(validation.errors.firstOrNull()?.message ?: "Validation failed"))
                return@flow
            }
            
            val result = authRepository.register(request)
            when (result) {
                is Result.Success -> emit(result)
                is Result.ServerError -> emit(result)
                else -> emit(Result.ServerError.General("Registration failed"))
            }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Registration failed"))
        }
    }
} 
