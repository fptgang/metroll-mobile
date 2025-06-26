package com.vidz.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.vidz.datastore.proto.UserData
import com.vidz.datastore.proto.UserPreferences
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

object UserPreferencesSerializer : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return UserPreferences.parseFrom(input)
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

private val Context.userDataStoreProto: DataStore<UserPreferences> by dataStore(
    fileName = "user_preferences.pb",
    serializer = UserPreferencesSerializer
)

@Singleton
class UserDataStoreProto @Inject constructor(
    @ApplicationContext private val context: Context
) {


    val userData: Flow<Account?> = context.userDataStoreProto.data.map { preferences ->
        if (preferences.isLoggedIn && preferences.hasUserData()) {
            val userData = preferences.userData
            Account(
                id = userData.id,
                email = userData.email,
                fullName = userData.fullName,
                phoneNumber = userData.phoneNumber,
                role = AccountRole.valueOf(userData.role),
                active = userData.active,
                createdAt = userData.createdAt,
                updatedAt = userData.updatedAt
            )
        } else {
            null
        }
    }

    val isLoggedIn: Flow<Boolean> = context.userDataStoreProto.data.map { preferences ->
        preferences.isLoggedIn && preferences.hasUserData()
    }

    suspend fun saveUser(account: Account) {
        context.userDataStoreProto.updateData { preferences ->
            val userData = UserData.newBuilder()
                .setId(account.id)
                .setEmail(account.email)
                .setFullName(account.fullName)
                .setPhoneNumber(account.phoneNumber)
                .setRole(account.role.name)
                .setActive(account.active)
                .setCreatedAt(account.createdAt)
                .setUpdatedAt(account.updatedAt)
                .build()

            preferences.toBuilder()
                .setUserData(userData)
                .setIsLoggedIn(true)
                .build()
        }
    }

    suspend fun clearUser() {
        context.userDataStoreProto.updateData { preferences ->
            preferences.toBuilder()
                .clearUserData()
                .setIsLoggedIn(false)
                .build()
        }
    }

    suspend fun updateLoginStatus(isLoggedIn: Boolean) {
        context.userDataStoreProto.updateData { preferences ->
            preferences.toBuilder()
                .setIsLoggedIn(isLoggedIn)
                .build()
        }
    }
} 
