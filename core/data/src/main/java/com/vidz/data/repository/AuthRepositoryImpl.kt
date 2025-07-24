package com.vidz.data.repository

import com.vidz.data.auth.FirebaseAuthDataSource
import com.vidz.data.flow.ServerFlow
import com.vidz.domain.Result
import com.vidz.domain.model.AuthToken
import com.vidz.domain.model.AuthenticationState
import com.vidz.domain.model.LoginCredentials
import com.vidz.domain.model.LoginRequest
import com.vidz.domain.model.LoginValidation
import com.vidz.domain.model.RegisterCredentials
import com.vidz.domain.model.RegisterRequest
import com.vidz.domain.model.User
import com.vidz.domain.model.ValidationError
import com.vidz.domain.model.ValidationErrors
import com.vidz.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    private val _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.Loading)
    private var currentAuthToken: AuthToken? = null
    private var currentUser: User? = null

    init {
        // Initialize with unauthenticated state
        _authenticationState.value = AuthenticationState.Unauthenticated
    }
    
    // Firebase methods
    override suspend fun loginWithEmailAndPassword(credentials: LoginCredentials): Flow<Result<User>> {
        return ServerFlow(
            getData = { firebaseAuthDataSource.loginWithEmailAndPassword(credentials) },
            convert = { user -> user }
        ).execute()
    }
    
    override suspend fun registerWithEmailAndPassword(credentials: RegisterCredentials): Flow<Result<User>> {
        return ServerFlow(
            getData = { firebaseAuthDataSource.registerWithEmailAndPassword(credentials) },
            convert = { user -> user }
        ).execute()
    }
    
    override suspend fun logout(): Flow<Result<Unit>> {
        return ServerFlow(
            getData = { firebaseAuthDataSource.logout() },
            convert = { }
        ).execute()
    }
    
    override suspend fun getCurrentUser(): Flow<Result<User?>> {
        return flow {
            emit(Result.Init)
            try {
                val user = firebaseAuthDataSource.getCurrentUser()
                emit(Result.Success(user))
            } catch (e: Exception) {
                emit(Result.ServerError.General(e.message ?: "Failed to get current user"))
            }
        }
    }
    
    override suspend fun sendPasswordResetEmail(email: String): Flow<Result<Unit>> {
        return ServerFlow(
            getData = { firebaseAuthDataSource.sendPasswordResetEmail(email) },
            convert = { Unit }
        ).execute()
    }
    
    override suspend fun deleteAccount(): Flow<Result<Unit>> {
        return ServerFlow(
            getData = { firebaseAuthDataSource.deleteAccount() },
            convert = { Unit }
        ).execute()
    }
    
    override fun isUserLoggedIn(): Boolean {
        return firebaseAuthDataSource.isUserLoggedIn()
    }
    
    // Legacy methods - keeping for backward compatibility
    override fun getAuthenticationState(): Flow<AuthenticationState> {
        return flow {
            emit(if (isUserLoggedIn()) {
                val user = firebaseAuthDataSource.getCurrentUser()
                if (user != null) {
                    AuthenticationState.Authenticated(user)
                } else {
                    AuthenticationState.Unauthenticated
                }
            } else {
                AuthenticationState.Unauthenticated
            })
        }
    }
    
    override suspend fun login(request: LoginRequest): Result<User> {
        return try {
            val credentials = LoginCredentials(request.email, request.password)
            val user = firebaseAuthDataSource.loginWithEmailAndPassword(credentials)
            Result.Success(user)
        } catch (e: Exception) {
            Result.ServerError.General(e.message ?: "Login failed")
        }
    }
    
    override suspend fun register(request: RegisterRequest): Result<User> {
        return try {
            val credentials = RegisterCredentials(
                email = request.email,
                password = request.password,
                confirmPassword = request.password, // Use password as confirmPassword since request doesn't have it
                displayName = "${request.firstName} ${request.lastName}".trim()
            )
            val user = firebaseAuthDataSource.registerWithEmailAndPassword(credentials)
            Result.Success(user)
        } catch (e: Exception) {
            Result.ServerError.General(e.message ?: "Registration failed")
        }
    }
    
    override suspend fun refreshToken(): Result<AuthToken> {
        // Firebase handles token refresh automatically
        return Result.ServerError.General("Token refresh not implemented for Firebase")
    }
    
    override suspend fun isAuthenticated(): Boolean {
        return isUserLoggedIn()
    }
    
    override suspend fun getAuthToken(): AuthToken? {
        // Firebase tokens are handled internally
        return null
    }
    
    override suspend fun saveAuthToken(token: AuthToken) {
        // Firebase handles tokens internally
    }
    
    override suspend fun clearAuthData() {
        firebaseAuthDataSource.logout()
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
        } else if (password.length < 6) {
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
        
        if (request.email.isBlank()) {
            errors.add(ValidationError("email", ValidationErrors.EMAIL_EMPTY))
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(request.email).matches()) {
            errors.add(ValidationError("email", ValidationErrors.EMAIL_INVALID))
        }
        
        if (request.password.isBlank()) {
            errors.add(ValidationError("password", ValidationErrors.PASSWORD_EMPTY))
        } else if (request.password.length < 6) {
            errors.add(ValidationError("password", ValidationErrors.PASSWORD_TOO_SHORT))
        }
        
        if (request.firstName.isBlank()) {
            errors.add(ValidationError("firstName", ValidationErrors.FIRST_NAME_EMPTY))
        }
        if (request.lastName.isBlank()) {
            errors.add(ValidationError("lastName", ValidationErrors.LAST_NAME_EMPTY))
        }
        
        return if (errors.isEmpty()) {
            LoginValidation.Valid
        } else {
            LoginValidation.Invalid(errors)
        }
    }
    
    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuthDataSource.sendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.ServerError.General(e.message ?: "Failed to send password reset email")
        }
    }
    
    override suspend fun resetPassword(email: String, otp: String, newPassword: String): Result<Unit> {
        return Result.ServerError.General("OTP password reset not implemented for Firebase")
    }
    
    override suspend fun verifyEmail(email: String, otp: String): Result<Unit> {
        return Result.ServerError.General("OTP email verification not implemented for Firebase")
    }
    
    override suspend fun setBiometricAuth(enabled: Boolean): Result<Unit> {
        return Result.ServerError.General("Biometric auth not implemented")
    }
    
    override suspend fun isBiometricAuthAvailable(): Boolean {
        return false
    }
    
    override suspend fun authenticateWithBiometrics(): Result<User> {
        return Result.ServerError.General("Biometric auth not implemented")
    }
} 
