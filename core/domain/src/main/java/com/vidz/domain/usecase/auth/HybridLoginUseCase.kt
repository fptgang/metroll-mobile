package com.vidz.domain.usecase.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.vidz.domain.Result
import com.vidz.domain.model.LoginCredentials
import com.vidz.domain.model.User
import com.vidz.domain.model.UserRole
import com.vidz.domain.repository.AccountManagementRepository
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HybridLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountManagementRepository: AccountManagementRepository,
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(email: String, password: String): Flow<Result<User>> = flow {
        emit(Result.Init)

        try {
            // Step 1: Login to Firebase
            val credentials = LoginCredentials(email = email, password = password)
            
            authRepository.loginWithEmailAndPassword(credentials).collect { firebaseResult ->
                when (firebaseResult) {
                    is Result.Init -> emit(Result.Init)
                    is Result.Success -> {
                        // Step 2: Get Firebase ID Token
                        val firebaseUser = firebaseAuth.currentUser
                        if (firebaseUser != null) {
                            try {
                                val idToken = firebaseUser.getIdToken(false).await()
                                Log.d("HybridLoginUseCase", "Firebase ID Token: ${idToken.token}")
                                var token = idToken.token
                                if (token != null) {
                                    // Step 3: Get current user profile from backend
                                    Log.d("HybridLoginUseCase", "Firebase user authenticated, token: $token")
                                    val backendResult = accountManagementRepository.login(idToken.token?:"").last()
                                    Log.d("HybridLoginUseCase", "Backend login result: $backendResult")
                                    token = firebaseUser.getIdToken(true).await()
                                            .toString()
                                    Log.d("HybridLoginUseCase", "Refreshed Firebase ID Token: ${token}")
                                    when (backendResult) {
                                        is Result.Success -> {
                                            val account = backendResult.data
                                            userRepository.saveUser(account)
                                            Log.d("HybridLoginUseCase", "User saved to repository: $account")
                                            val user = User(
                                                uid = account.id,
                                                email = account.email,
                                                displayName = account.fullName,
                                                photoUrl = null,
                                                isEmailVerified = account.active,
                                                createdAt = null,
                                                role = mapAccountRoleToUserRole(account.role)
                                            )
                                            emit(Result.Success(user))
                                        }
                                        is Result.ServerError -> {
                                            emit(Result.Success(firebaseResult.data))
                                        }
                                        else -> emit(Result.Success(firebaseResult.data))
                                    }
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
            emit(Result.ServerError.General(e.message ?: "Authentication failed"))
        }
    }
    
    private fun mapAccountRoleToUserRole(accountRole: com.vidz.domain.model.AccountRole): UserRole {
        return when (accountRole) {
            com.vidz.domain.model.AccountRole.ADMIN -> UserRole.ADMIN
            com.vidz.domain.model.AccountRole.STAFF -> UserRole.STAFF
            com.vidz.domain.model.AccountRole.CUSTOMER -> UserRole.CUSTOMER
        }
    }
} 
