package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Init)
            
            if (email.isBlank()) {
                emit(Result.ServerError.MissingParam("Email is required"))
                return@flow
            }
            
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emit(Result.ServerError.MissingParam("Please enter a valid email address"))
                return@flow
            }
            
            val result = authRepository.requestPasswordReset(email)
            when (result) {
                is Result.Success -> emit(result)
                is Result.ServerError -> emit(result)
                else -> emit(Result.ServerError.General("Failed to send reset email"))
            }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to send reset email"))
        }
    }
}
