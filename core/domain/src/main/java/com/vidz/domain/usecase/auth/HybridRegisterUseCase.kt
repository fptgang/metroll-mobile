package com.vidz.domain.usecase.auth

import com.google.firebase.auth.FirebaseAuth
import com.vidz.domain.Result
import com.vidz.domain.model.RegisterCredentials
import com.vidz.domain.model.User
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HybridRegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth
) {
     operator fun invoke(
        email: String, 
        password: String, 
        confirmPassword: String,
        displayName: String? = null
    ): Flow<Result<User>> = flow {
        emit(Result.Init)
        
        try {
            val credentials = RegisterCredentials(
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                displayName = displayName
            )
            
            authRepository.registerWithEmailAndPassword(credentials).collect { firebaseResult ->
                when (firebaseResult) {
                    is Result.Init -> emit(Result.Init)
                    is Result.Success -> {
                        val firebaseUser = firebaseAuth.currentUser
                        if (firebaseUser != null) {
                            try {
                                val idToken = firebaseUser.getIdToken(false).await()
                                val token = idToken.token
                                
                                if (token != null) {
                                    // TODO: Call backend registration API with token
                                    emit(Result.Success(firebaseResult.data))
                                } else {
                                    emit(Result.ServerError.Token("Failed to get Firebase token"))
                                }
                            } catch (e: Exception) {
                                emit(Result.ServerError.Token("Failed to get Firebase token: ${e.message}"))
                            }
                        } else {
                            emit(Result.ServerError.General("No authenticated user found"))
                        }
                    }
                    is Result.ServerError -> emit(firebaseResult)
                }
            }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Registration failed"))
        }
    }
} 