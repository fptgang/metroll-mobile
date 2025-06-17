package com.vidz.domain.model

import java.util.Date

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null,
    val role: UserRole,
    val isEmailVerified: Boolean = false,
    val createdAt: Date,
    val lastLoginAt: Date? = null,
    val preferences: UserPreferences = UserPreferences()
) {
    val fullName: String
        get() = "$firstName $lastName"
    
    val initials: String
        get() = "${firstName.firstOrNull()?.uppercase()}${lastName.firstOrNull()?.uppercase()}"
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