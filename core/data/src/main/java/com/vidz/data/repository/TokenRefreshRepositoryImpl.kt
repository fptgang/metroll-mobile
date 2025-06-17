package com.vidz.data.repository

import com.vidz.data.server.retrofit.api.RefreshTokenRequest
import com.vidz.data.server.retrofit.api.TokenRefreshApi
import com.vidz.domain.Result
import com.vidz.domain.Result.ServerError
import com.vidz.domain.Result.Success
import com.vidz.domain.repository.TokenRefreshRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TokenRefreshRepositoryImpl @Inject constructor(
    private val tokenRefreshApi: TokenRefreshApi
) : TokenRefreshRepository {

    override suspend fun refreshToken(refreshToken: String): Flow<Result<String>> = 
        flow {
            val response = tokenRefreshApi.refreshToken(RefreshTokenRequest(refreshToken))
            
            if (response.isSuccessful && response.body() != null) {
                val newAccessToken = response.body()!!.token
                emit(Success(newAccessToken))
            } else {
                emit(ServerError.RequiredLogin("Token refresh failed: HTTP ${response.code()}"))
            }
        }.catch { exception ->
            emit(ServerError.RequiredLogin("Token refresh failed: ${exception.message}"))
        }
} 
