package com.vidz.domain.repository

import com.vidz.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<Account?>
    suspend fun saveUser(account: Account)
    suspend fun clearUser()
    fun isUserLoggedIn(): Flow<Boolean>
} 