package com.vidz.datastore.repository

import com.vidz.datastore.UserDataStoreProto
import com.vidz.domain.model.Account
import com.vidz.domain.repository.UserLocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalRepositoryImpl @Inject constructor(
    private val userDataStoreProto: UserDataStoreProto
) : UserLocalRepository {

    override fun observeUserData(): Flow<Account?> {
        return userDataStoreProto.userData
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return userDataStoreProto.isLoggedIn
    }

    override suspend fun saveUser(account: Account) {
        userDataStoreProto.saveUser(account)
    }

    override suspend fun clearUser() {
        userDataStoreProto.clearUser()
    }

    override suspend fun updateLoginStatus(isLoggedIn: Boolean) {
        userDataStoreProto.updateLoginStatus(isLoggedIn)
    }
} 
