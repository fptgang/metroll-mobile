package com.vidz.datastore

import com.vidz.domain.Result
import com.vidz.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager
) : TokenRepository {

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
    }

    override suspend fun clearTokens() {
        tokenManager.clearTokens()
    }

    override fun isAuthenticated(): Flow<Boolean> {
        return tokenManager.isAuthenticated()
    }

    override suspend fun getValidAccessToken(): Flow<Result<String>> {
        return tokenManager.getValidAccessToken()
    }

    override suspend fun handleTokenExpired(): Flow<Result<String>> {
        return tokenManager.handleTokenExpired()
    }
} 