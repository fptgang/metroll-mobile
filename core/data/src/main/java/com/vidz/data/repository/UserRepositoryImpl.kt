package com.vidz.data.repository

import com.vidz.datastore.UserDataStore
import com.vidz.domain.model.Account
import com.vidz.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataStore: UserDataStore
) : UserRepository {
    
    override fun getCurrentUser(): Flow<Account?> {
        return userDataStore.userData
    }
    
    override suspend fun saveUser(account: Account) {
        userDataStore.saveUser(account)
    }
    
    override suspend fun clearUser() {
        userDataStore.clearUser()
    }
    
    override fun isUserLoggedIn(): Flow<Boolean> {
        return userDataStore.isUserLoggedIn()
    }
} 