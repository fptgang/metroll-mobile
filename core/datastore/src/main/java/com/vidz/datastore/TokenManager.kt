package com.vidz.datastore

import com.vidz.domain.Result
import com.vidz.domain.Result.Success
import com.vidz.domain.repository.TokenRefreshRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val tokenRefreshRepository: TokenRefreshRepository
) {
    
    // Mutex to prevent concurrent token refresh attempts
    private val refreshMutex = Mutex()
    private var isRefreshing = false

    fun isAuthenticated(): Flow<Boolean> {
        return tokenDataStore.getAccessToken().map { accessToken ->
            !accessToken.isNullOrEmpty()
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenDataStore.saveTokens(accessToken, refreshToken)
    }

    suspend fun getValidAccessToken(): Flow<Result<String>> = flow {
        val accessToken = tokenDataStore.getAccessToken().first()
        
        if (accessToken.isNullOrEmpty()) {
            emit(Result.ServerError.RequiredLogin("No access token available"))
            return@flow
        }

        // For simplicity, we're returning the token as is
        // In a real app, you might want to check if the token is expired
        // before returning it or attempting to refresh it
        emit(Success(accessToken))
    }.catch { exception ->
        emit(Result.ServerError.General("Failed to get access token: ${exception.message}"))
    }

    suspend fun refreshAccessToken(): Flow<Result<String>> = flow {
        refreshMutex.withLock {
            // Check if another coroutine is already refreshing
            if (isRefreshing) {
                // Return the current access token if refresh is in progress
                val currentToken = tokenDataStore.getAccessToken().first()
                if (!currentToken.isNullOrEmpty()) {
                    emit(Success(currentToken))
                    return@flow
                } else {
                    emit(Result.ServerError.RequiredLogin("Token refresh in progress"))
                    return@flow
                }
            }
            
            isRefreshing = true
            
            try {
                val refreshToken = tokenDataStore.getRefreshToken().first()
                
                if (refreshToken.isNullOrEmpty()) {
                    emit(Result.ServerError.RequiredLogin("No refresh token available"))
                    return@flow
                }

                // Use flatMapLatest to avoid flow transparency violations
                val refreshResult = try {
                    tokenRefreshRepository.refreshToken(refreshToken).first()
                } catch (e: Exception) {
                    Result.ServerError.General("Token refresh failed: ${e.message}")
                }
                
                when (refreshResult) {
                    is Success -> {
                        // Save the new access token
                        tokenDataStore.saveAccessToken(refreshResult.data)
                        emit(Success(refreshResult.data))
                    }
                    is Result.ServerError -> {
                        // If refresh fails, clear all tokens
                        clearTokens()
                        emit(refreshResult)
                    }
                    else -> emit(refreshResult)
                }
            } catch (exception: Exception) {
                // Clear tokens on any error
                clearTokens()
                emit(Result.ServerError.General("Token refresh failed: ${exception.message}"))
            } finally {
                isRefreshing = false
            }
        }
    }

    suspend fun clearTokens() {
        tokenDataStore.clearTokens()
    }

    suspend fun handleTokenExpired(): Flow<Result<String>> {
        return refreshAccessToken()
    }
} 
