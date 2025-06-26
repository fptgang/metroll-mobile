package com.vidz.domain.repository

import com.vidz.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface UserLocalRepository {
    fun observeUserData(): Flow<Account?>
    fun isLoggedIn(): Flow<Boolean>
    suspend fun saveUser(account: Account)
    suspend fun clearUser()
    suspend fun updateLoginStatus(isLoggedIn: Boolean)
} 
