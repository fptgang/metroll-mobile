package com.vidz.domain.repository

import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow

interface TokenRefreshRepository {
    suspend fun refreshToken(refreshToken: String): Flow<Result<String>>
} 