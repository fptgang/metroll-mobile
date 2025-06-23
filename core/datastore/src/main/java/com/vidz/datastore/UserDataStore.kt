package com.vidz.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_FULL_NAME = stringPreferencesKey("user_full_name")
        val USER_PHONE_NUMBER = stringPreferencesKey("user_phone_number")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_ACTIVE = booleanPreferencesKey("user_active")
        val USER_CREATED_AT = stringPreferencesKey("user_created_at")
        val USER_UPDATED_AT = stringPreferencesKey("user_updated_at")
    }

    val userData: Flow<Account?> = context.userDataStore.data
        .map { preferences ->
            val userId = preferences[PreferencesKeys.USER_ID]
            if (userId != null) {
                Account(
                    id = userId,
                    email = preferences[PreferencesKeys.USER_EMAIL] ?: "",
                    fullName = preferences[PreferencesKeys.USER_FULL_NAME] ?: "",
                    phoneNumber = preferences[PreferencesKeys.USER_PHONE_NUMBER] ?: "",
                    role = AccountRole.valueOf(
                        preferences[PreferencesKeys.USER_ROLE] ?: AccountRole.CUSTOMER.name
                    ),
                    active = preferences[PreferencesKeys.USER_ACTIVE] ?: true,
                    createdAt = preferences[PreferencesKeys.USER_CREATED_AT] ?: "",
                    updatedAt = preferences[PreferencesKeys.USER_UPDATED_AT] ?: ""
                )
            } else {
                null
            }
        }

    suspend fun saveUser(account: Account) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = account.id
            preferences[PreferencesKeys.USER_EMAIL] = account.email
            preferences[PreferencesKeys.USER_FULL_NAME] = account.fullName
            preferences[PreferencesKeys.USER_PHONE_NUMBER] = account.phoneNumber
            preferences[PreferencesKeys.USER_ROLE] = account.role.name
            preferences[PreferencesKeys.USER_ACTIVE] = account.active
            preferences[PreferencesKeys.USER_CREATED_AT] = account.createdAt
            preferences[PreferencesKeys.USER_UPDATED_AT] = account.updatedAt
        }
    }

    suspend fun clearUser() {
        context.userDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
            preferences.remove(PreferencesKeys.USER_EMAIL)
            preferences.remove(PreferencesKeys.USER_FULL_NAME)
            preferences.remove(PreferencesKeys.USER_PHONE_NUMBER)
            preferences.remove(PreferencesKeys.USER_ROLE)
            preferences.remove(PreferencesKeys.USER_ACTIVE)
            preferences.remove(PreferencesKeys.USER_CREATED_AT)
            preferences.remove(PreferencesKeys.USER_UPDATED_AT)
        }
    }

    fun isUserLoggedIn(): Flow<Boolean> {
        return context.userDataStore.data.map { preferences ->
            !preferences[PreferencesKeys.USER_ID].isNullOrEmpty()
        }
    }
} 
