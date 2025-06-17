package com.vidz.domain.model

sealed class AuthenticationState {
    object Loading : AuthenticationState()
    object Unauthenticated : AuthenticationState()
    data class Authenticated(val user: User) : AuthenticationState()
    data class Error(val message: String) : AuthenticationState()
}

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val role: UserRole = UserRole.CUSTOMER
)

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val tokenType: String = "Bearer"
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() >= expiresAt
}

// Login validation results
sealed class LoginValidation {
    object Valid : LoginValidation()
    data class Invalid(val errors: List<ValidationError>) : LoginValidation()
}

data class ValidationError(
    val field: String,
    val message: String
)

// Common validation errors
object ValidationErrors {
    const val EMAIL_EMPTY = "Email is required"
    const val EMAIL_INVALID = "Please enter a valid email address"
    const val PASSWORD_EMPTY = "Password is required"
    const val PASSWORD_TOO_SHORT = "Password must be at least 8 characters"
    const val PASSWORD_WEAK = "Password must contain uppercase, lowercase, and numbers"
    const val FIRST_NAME_EMPTY = "First name is required"
    const val LAST_NAME_EMPTY = "Last name is required"
    const val PHONE_INVALID = "Please enter a valid phone number"
} 