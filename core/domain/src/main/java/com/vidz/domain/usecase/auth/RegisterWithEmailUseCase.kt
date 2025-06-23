package com.vidz.domain.usecase.auth

import com.vidz.domain.Result
import com.vidz.domain.model.RegisterCredentials
import com.vidz.domain.model.User
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String, 
        password: String, 
        confirmPassword: String,
        displayName: String? = null
    ): Flow<Result<User>> {
        val credentials = RegisterCredentials(
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            displayName = displayName
        )
        return authRepository.registerWithEmailAndPassword(credentials)
    }
} 