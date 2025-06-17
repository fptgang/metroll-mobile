package com.vidz.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

@Singleton
class AppPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val PUSH_NOTIFICATIONS_ENABLED = booleanPreferencesKey("push_notifications_enabled")
        val EMAIL_NOTIFICATIONS_ENABLED = booleanPreferencesKey("email_notifications_enabled")
        val BIOMETRIC_AUTH_ENABLED = booleanPreferencesKey("biometric_auth_enabled")
        val AUTO_SYNC_ENABLED = booleanPreferencesKey("auto_sync_enabled")
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
    }

    val appSettings: Flow<AppSettings> = context.dataStore.data
        .map { preferences ->
            AppSettings(
                themeMode = ThemeMode.valueOf(
                    preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                pushNotificationsEnabled = preferences[PreferencesKeys.PUSH_NOTIFICATIONS_ENABLED] ?: true,
                emailNotificationsEnabled = preferences[PreferencesKeys.EMAIL_NOTIFICATIONS_ENABLED] ?: true,
                biometricAuthEnabled = preferences[PreferencesKeys.BIOMETRIC_AUTH_ENABLED] ?: false,
                autoSyncEnabled = preferences[PreferencesKeys.AUTO_SYNC_ENABLED] ?: true,
                language = preferences[PreferencesKeys.LANGUAGE] ?: "en",
                currency = preferences[PreferencesKeys.CURRENCY] ?: "USD"
            )
        }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updatePushNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PUSH_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateEmailNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EMAIL_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateBiometricAuthEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRIC_AUTH_ENABLED] = enabled
        }
    }

    suspend fun updateAutoSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SYNC_ENABLED] = enabled
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language
        }
    }

    suspend fun updateCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY] = currency
        }
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val biometricAuthEnabled: Boolean = false,
    val autoSyncEnabled: Boolean = true,
    val language: String = "en",
    val currency: String = "USD"
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    VIETNAMESE("vi", "Tiếng Việt"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    CHINESE("zh", "中文")
}

enum class Currency(val code: String, val symbol: String, val displayName: String) {
    USD("USD", "$", "US Dollar"),
    VND("VND", "₫", "Vietnamese Dong"),
    JPY("JPY", "¥", "Japanese Yen"),
    KRW("KRW", "₩", "Korean Won"),
    CNY("CNY", "¥", "Chinese Yuan")
} 