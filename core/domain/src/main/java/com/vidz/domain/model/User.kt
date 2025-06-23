package com.vidz.domain.model

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false,
    val createdAt: Long? = null,
    val role: UserRole = UserRole.CUSTOMER,
    val preferences: UserPreferences = UserPreferences()
) {
    val fullName: String
        get() = displayName ?: "Unknown User"
    
    val initials: String
        get() = displayName?.split(" ")?.mapNotNull { it.firstOrNull()?.uppercase() }?.take(2)?.joinToString("") ?: "U"
}

enum class UserRole(val displayName: String) {
    CUSTOMER("Customer"),
    STAFF("Staff"),
    ADMIN("Administrator");
    
    val isStaff: Boolean
        get() = this == STAFF || this == ADMIN
    
    val isCustomer: Boolean
        get() = this == CUSTOMER
}

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val biometricAuthEnabled: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "en"
)

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
} 
