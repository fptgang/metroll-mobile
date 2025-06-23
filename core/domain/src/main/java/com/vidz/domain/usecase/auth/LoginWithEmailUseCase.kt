package com.vidz.domain.usecase.auth

import com.vidz.domain.Result
import com.vidz.domain.model.LoginCredentials
import com.vidz.domain.model.User
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Flow<Result<User>> {
        val credentials = LoginCredentials(email = email, password = password)
        return authRepository.loginWithEmailAndPassword(credentials)
    }
} 