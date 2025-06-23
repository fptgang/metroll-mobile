package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.AuthToken
import com.vidz.domain.model.AuthenticationState
import com.vidz.domain.model.LoginCredentials
import com.vidz.domain.model.LoginRequest
import com.vidz.domain.model.LoginValidation
import com.vidz.domain.model.RegisterCredentials
import com.vidz.domain.model.RegisterRequest
import com.vidz.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    
    /**
     * Get current authentication state as a flow
     */
    fun getAuthenticationState(): Flow<AuthenticationState>
    
    /**
     * Get current authenticated user
     */
    suspend fun getCurrentUser():Flow<Result<User?>>
    
    /**
     * Login with email and password
     */
    suspend fun login(request: LoginRequest): Result<User>
    
    /**
     * Register new user account
     */
    suspend fun register(request: RegisterRequest): Result<User>
    
    /**
     * Logout current user
     */
    suspend fun logout(): Flow<Result<Unit>>
    
    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(): Result<AuthToken>
    
    /**
     * Check if user is currently authenticated
     */
    suspend fun isAuthenticated(): Boolean
    
    /**
     * Get stored authentication token
     */
    suspend fun getAuthToken(): AuthToken?
    
    /**
     * Save authentication token securely
     */
    suspend fun saveAuthToken(token: AuthToken)
    
    /**
     * Clear all authentication data
     */
    suspend fun clearAuthData()
    
    /**
     * Validate login credentials
     */
    fun validateLoginCredentials(email: String, password: String): LoginValidation
    
    /**
     * Validate registration data
     */
    fun validateRegistrationData(request: RegisterRequest): LoginValidation
    
    /**
     * Request password reset
     */
    suspend fun requestPasswordReset(email: String): Result<Unit>
    
    /**
     * Reset password with OTP
     */
    suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Unit>
    
    /**
     * Verify email with OTP
     */
    suspend fun verifyEmail(email: String, otp: String): Result<Unit>
    
    /**
     * Enable/disable biometric authentication
     */
    suspend fun setBiometricAuth(enabled: Boolean): Result<Unit>
    
    /**
     * Check if biometric authentication is available
     */
    suspend fun isBiometricAuthAvailable(): Boolean
    
    /**
     * Authenticate with biometrics
     */
    suspend fun authenticateWithBiometrics(): Result<User>

    suspend fun loginWithEmailAndPassword(credentials: LoginCredentials): Flow<Result<User>>
    suspend fun registerWithEmailAndPassword(credentials: RegisterCredentials): Flow<Result<User>>
    suspend fun sendPasswordResetEmail(email: String): Flow<Result<Unit>>
    suspend fun deleteAccount(): Flow<Result<Unit>>
    fun isUserLoggedIn(): Boolean
}
