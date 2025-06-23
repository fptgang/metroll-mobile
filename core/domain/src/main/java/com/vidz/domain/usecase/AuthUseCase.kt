package com.vidz.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.AuthToken
import com.vidz.domain.model.AuthenticationState
import com.vidz.domain.model.LoginRequest
import com.vidz.domain.model.LoginValidation
import com.vidz.domain.model.RegisterRequest
import com.vidz.domain.model.User
import com.vidz.domain.model.UserRole
import com.vidz.domain.model.ValidationErrors
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    /**
     * Get authentication state
     */
    fun getAuthenticationState(): Flow<AuthenticationState> {
        return authRepository.getAuthenticationState()
    }
    
    /**
     * Login user with credentials
     */
    suspend fun login(email: String, password: String, rememberMe: Boolean = false): Flow<Result<User>> = flow {
        try {
            emit(Result.Init)
            
            // Validate credentials first
            val validation = authRepository.validateLoginCredentials(email.trim(), password)
            if (validation is LoginValidation.Invalid) {
                emit(Result.ServerError.MissingParam(validation.errors.first().message))
                return@flow
            }
            
            val request = LoginRequest(
                email = email.trim().lowercase(),
                password = password,
                rememberMe = rememberMe
            )
            
            val result = authRepository.login(request)
            when (result) {
                is Result.Success -> emit(result)
                is Result.ServerError -> emit(result)
                else -> emit(Result.ServerError.General("Login failed"))
            }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Login failed"))
        }
    }
    
    /**
     * Register new user
     */
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String? = null,
        role: UserRole = UserRole.CUSTOMER
    ): Result<User> {
        val request = RegisterRequest(
            email = email.trim().lowercase(),
            password = password,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            phoneNumber = phoneNumber?.trim(),
            role = role
        )
        
        // Validate registration data
        val validation = authRepository.validateRegistrationData(request)
        if (validation is LoginValidation.Invalid) {
            return Result.ServerError.MissingParam(validation.errors.first().message)
        }
        
        return authRepository.register(request)
    }
    
    /**
     * Logout current user
     */
    suspend fun logout(): Flow<Result<Unit>> {
        return authRepository.logout()
    }
    
    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean {
        return authRepository.isAuthenticated()
    }
    
    /**
     * Get current user
     */
    suspend fun getCurrentUser(): Flow<Result<User?>>{
        return authRepository.getCurrentUser()
    }
    
    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(): Result<AuthToken> {
        return authRepository.refreshToken()
    }
    
    /**
     * Request password reset
     */
    suspend fun requestPasswordReset(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.ServerError.MissingParam(ValidationErrors.EMAIL_EMPTY)
        }
        
        if (!isValidEmail(email)) {
            return Result.ServerError.MissingParam(ValidationErrors.EMAIL_INVALID)
        }
        
        return authRepository.requestPasswordReset(email.trim().lowercase())
    }
    
    /**
     * Reset password with OTP
     */
    suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.ServerError.MissingParam(ValidationErrors.EMAIL_EMPTY)
        }
        
        if (otp.isBlank()) {
            return Result.ServerError.MissingParam("OTP is required")
        }
        
        if (newPassword.length < 8) {
            return Result.ServerError.MissingParam(ValidationErrors.PASSWORD_TOO_SHORT)
        }
        
        return authRepository.resetPassword(email.trim().lowercase(), otp, newPassword)
    }
    
    /**
     * Verify email with OTP
     */
    suspend fun verifyEmail(email: String, otp: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.ServerError.MissingParam(ValidationErrors.EMAIL_EMPTY)
        }
        
        if (otp.isBlank()) {
            return Result.ServerError.MissingParam("OTP is required")
        }
        
        return authRepository.verifyEmail(email.trim().lowercase(), otp)
    }
    
    /**
     * Enable/disable biometric authentication
     */
    suspend fun setBiometricAuth(enabled: Boolean): Result<Unit> {
        if (enabled && !authRepository.isBiometricAuthAvailable()) {
            return Result.ServerError.General("Biometric authentication is not available on this device")
        }
        
        return authRepository.setBiometricAuth(enabled)
    }
    
    /**
     * Authenticate with biometrics
     */
    suspend fun authenticateWithBiometrics(): Flow<Result<User>> = flow {
        try {
            emit(Result.Init)
            
            if (!authRepository.isBiometricAuthAvailable()) {
                emit(Result.ServerError.General("Biometric authentication is not available"))
                return@flow
            }
            
            val result = authRepository.authenticateWithBiometrics()
            when (result) {
                is Result.Success -> emit(result)
                is Result.ServerError -> emit(result)
                else -> emit(Result.ServerError.General("Biometric authentication failed"))
            }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Biometric authentication failed"))
        }
    }
    
    /**
     * Get user's navigation destination based on role
     */
    fun getHomeDestinationForRole(role: UserRole): String {
        return when (role) {
            UserRole.STAFF -> "staff_home"
            UserRole.CUSTOMER -> "customer_home"
            UserRole.ADMIN -> "admin_dashboard"
        }
    }
    


    
    // Private validation helpers
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        
        return hasUppercase && hasLowercase && hasDigit
    }
} 
