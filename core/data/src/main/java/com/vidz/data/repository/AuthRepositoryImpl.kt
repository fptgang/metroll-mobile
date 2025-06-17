package com.vidz.data.repository

import com.vidz.domain.Result
import com.vidz.domain.model.AuthToken
import com.vidz.domain.model.AuthenticationState
import com.vidz.domain.model.LoginRequest
import com.vidz.domain.model.LoginValidation
import com.vidz.domain.model.RegisterRequest
import com.vidz.domain.model.User
import com.vidz.domain.model.UserRole
import com.vidz.domain.model.ValidationError
import com.vidz.domain.model.ValidationErrors
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    // TODO: Inject actual data sources (API, DataStore, etc.)
) : AuthRepository {

    private val _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.Loading)
    private var currentAuthToken: AuthToken? = null
    private var currentUser: User? = null
    
    // Mock users for development
    private val mockUsers = listOf(
        User(
            id = "customer_1",
            email = "customer@metroll.com",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+1234567890",
            role = UserRole.CUSTOMER,
            isEmailVerified = true,
            createdAt = Date(),
            lastLoginAt = Date()
        ),
        User(
            id = "staff_1",
            email = "staff@metroll.com",
            firstName = "Jane",
            lastName = "Smith",
            phoneNumber = "+1234567891",
            role = UserRole.STAFF,
            isEmailVerified = true,
            createdAt = Date(),
            lastLoginAt = Date()
        ),
        User(
            id = "admin_1",
            email = "admin@metroll.com",
            firstName = "Admin",
            lastName = "User",
            phoneNumber = "+1234567892",
            role = UserRole.ADMIN,
            isEmailVerified = true,
            createdAt = Date(),
            lastLoginAt = Date()
        )
    )
    
    init {
        // Initialize with unauthenticated state
        _authenticationState.value = AuthenticationState.Unauthenticated
    }
    
    override fun getAuthenticationState(): Flow<AuthenticationState> {
        return _authenticationState.asStateFlow()
    }
    
    override suspend fun getCurrentUser(): User? {
        return currentUser
    }
    
    override suspend fun login(request: LoginRequest): Result<User> {
        try {
            _authenticationState.value = AuthenticationState.Loading
            
            // Simulate network delay
            delay(1500)
            
            // Mock authentication logic
            val user = mockUsers.find { it.email == request.email }
            
            if (user == null) {
                _authenticationState.value = AuthenticationState.Unauthenticated
                return Result.ServerError.General("Invalid email or password")
            }
            
            // Mock password check (in real app, this would be done server-side)
            if (request.password != "password123") {
                _authenticationState.value = AuthenticationState.Unauthenticated
                return Result.ServerError.General("Invalid email or password")
            }
            
            // Create mock token
            val token = AuthToken(
                accessToken = "mock_access_token_${user.id}",
                refreshToken = "mock_refresh_token_${user.id}",
                expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
            )
            
            // Update state
            currentAuthToken = token
            currentUser = user.copy(lastLoginAt = Date())
            _authenticationState.value = AuthenticationState.Authenticated(currentUser!!)
            
            return Result.Success(currentUser!!)
            
        } catch (e: Exception) {
            _authenticationState.value = AuthenticationState.Error(e.message ?: "Login failed")
            return Result.ServerError.General(e.message ?: "Login failed")
        }
    }
    
    override suspend fun register(request: RegisterRequest): Result<User> {
        try {
            _authenticationState.value = AuthenticationState.Loading
            
            // Simulate network delay
            delay(2000)
            
            // Check if email already exists
            val existingUser = mockUsers.find { it.email == request.email }
            if (existingUser != null) {
                _authenticationState.value = AuthenticationState.Unauthenticated
                return Result.ServerError.General("Email already registered")
            }
            
            // Create new user
            val newUser = User(
                id = "user_${System.currentTimeMillis()}",
                email = request.email,
                firstName = request.firstName,
                lastName = request.lastName,
                phoneNumber = request.phoneNumber,
                role = request.role,
                isEmailVerified = false,
                createdAt = Date(),
                lastLoginAt = Date()
            )
            
            // Create mock token
            val token = AuthToken(
                accessToken = "mock_access_token_${newUser.id}",
                refreshToken = "mock_refresh_token_${newUser.id}",
                expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
            )
            
            // Update state
            currentAuthToken = token
            currentUser = newUser
            _authenticationState.value = AuthenticationState.Authenticated(newUser)
            
            return Result.Success(newUser)
            
        } catch (e: Exception) {
            _authenticationState.value = AuthenticationState.Error(e.message ?: "Registration failed")
            return Result.ServerError.General(e.message ?: "Registration failed")
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        try {
            // Simulate network delay
            delay(500)
            
            // Clear local data
            currentAuthToken = null
            currentUser = null
            _authenticationState.value = AuthenticationState.Unauthenticated
            
            return Result.Success(Unit)
            
        } catch (e: Exception) {
            return Result.ServerError.General("Logout failed")
        }
    }
    
    override suspend fun refreshToken(): Result<AuthToken> {
        try {
            val currentToken = currentAuthToken
                ?: return Result.ServerError.Token("No token to refresh")
            
            // Simulate network delay
            delay(1000)
            
            // Create new token
            val newToken = currentToken.copy(
                accessToken = "refreshed_${currentToken.accessToken}",
                expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
            )
            
            currentAuthToken = newToken
            return Result.Success(newToken)
            
        } catch (e: Exception) {
            return Result.ServerError.Token("Token refresh failed")
        }
    }
    
    override suspend fun isAuthenticated(): Boolean {
        val token = currentAuthToken
        return token != null && !token.isExpired && currentUser != null
    }
    
    override suspend fun getAuthToken(): AuthToken? {
        return currentAuthToken
    }
    
    override suspend fun saveAuthToken(token: AuthToken) {
        currentAuthToken = token
        // TODO: Save to secure storage (DataStore/Keystore)
    }
    
    override suspend fun clearAuthData() {
        currentAuthToken = null
        currentUser = null
        _authenticationState.value = AuthenticationState.Unauthenticated
    }
    
    override fun validateLoginCredentials(email: String, password: String): LoginValidation {
        val errors = mutableListOf<ValidationError>()
        
        if (email.isBlank()) {
            errors.add(ValidationError("email", ValidationErrors.EMAIL_EMPTY))
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add(ValidationError("email", ValidationErrors.EMAIL_INVALID))
        }
        
        if (password.isBlank()) {
            errors.add(ValidationError("password", ValidationErrors.PASSWORD_EMPTY))
        } else if (password.length < 8) {
            errors.add(ValidationError("password", ValidationErrors.PASSWORD_TOO_SHORT))
        }
        
        return if (errors.isEmpty()) {
            LoginValidation.Valid
        } else {
            LoginValidation.Invalid(errors)
        }
    }
    
    override fun validateRegistrationData(request: RegisterRequest): LoginValidation {
        val errors = mutableListOf<ValidationError>()
        
        // Email validation
        if (request.email.isBlank()) {
            errors.add(ValidationError("email", ValidationErrors.EMAIL_EMPTY))
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            errors.add(ValidationError("email", ValidationErrors.EMAIL_INVALID))
        }
        
        // Password validation
        if (request.password.isBlank()) {
            errors.add(ValidationError("password", ValidationErrors.PASSWORD_EMPTY))
        } else if (request.password.length < 8) {
            errors.add(ValidationError("password", ValidationErrors.PASSWORD_TOO_SHORT))
        }
        
        // Name validation
        if (request.firstName.isBlank()) {
            errors.add(ValidationError("firstName", ValidationErrors.FIRST_NAME_EMPTY))
        }
        
        if (request.lastName.isBlank()) {
            errors.add(ValidationError("lastName", ValidationErrors.LAST_NAME_EMPTY))
        }
        
//        // Phone validation (optional)
//        if (!request.phoneNumber.isNullOrBlank() && request.phoneNumber.length < 10) {
//            errors.add(ValidationError("phoneNumber", ValidationErrors.PHONE_INVALID))
//        }
        
        return if (errors.isEmpty()) {
            LoginValidation.Valid
        } else {
            LoginValidation.Invalid(errors)
        }
    }
    
    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        // Simulate network delay
        delay(1000)
        
        // Mock implementation - always succeed for development
        return Result.Success(Unit)
    }
    
    override suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Unit> {
        // Simulate network delay
        delay(1000)
        
        // Mock implementation - always succeed for development
        return Result.Success(Unit)
    }
    
    override suspend fun verifyEmail(email: String, otp: String): Result<Unit> {
        // Simulate network delay
        delay(1000)
        
        // Mock implementation - always succeed for development
        currentUser = currentUser?.copy(isEmailVerified = true)
        return Result.Success(Unit)
    }
    
    override suspend fun setBiometricAuth(enabled: Boolean): Result<Unit> {
        // Mock implementation - always succeed for development
        return Result.Success(Unit)
    }
    
    override suspend fun isBiometricAuthAvailable(): Boolean {
        // Mock implementation - assume available for development
        return true
    }
    
    override suspend fun authenticateWithBiometrics(): Result<User> {
        try {
            // Simulate biometric authentication delay
            delay(1500)
            
            // Mock implementation - use first customer for development
            val user = mockUsers.first { it.role == UserRole.CUSTOMER }
            
            // Create mock token
            val token = AuthToken(
                accessToken = "biometric_token_${user.id}",
                refreshToken = "biometric_refresh_${user.id}",
                expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
            )
            
            // Update state
            currentAuthToken = token
            currentUser = user.copy(lastLoginAt = Date())
            _authenticationState.value = AuthenticationState.Authenticated(currentUser!!)
            
            return Result.Success(currentUser!!)
            
        } catch (e: Exception) {
            return Result.ServerError.General("Biometric authentication failed")
        }
    }
} 
