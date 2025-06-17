package com.vidz.domain.repository

import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
    fun isAuthenticated(): Flow<Boolean>
    suspend fun getValidAccessToken(): Flow<Result<String>>
    suspend fun handleTokenExpired(): Flow<Result<String>>
} 