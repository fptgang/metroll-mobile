package com.vidz.data.server.retrofit.api

import com.vidz.data.server.retrofit.dto.AccountDto
import com.vidz.data.server.retrofit.dto.RefreshTokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @GET("accounts/me/")
    suspend fun getCurrentUser(): Response<AccountDto>

    @POST("accounts/login/")
    suspend fun login(): Response<AccountDto>

    @POST("auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<Unit>

    @POST("auth/login-with-google")
    suspend fun loginWithGoogle(
        @Body googleLoginRequest: GoogleLoginRequest
    ): Response<LoginResponse>

    @POST("auth/login-with-firebase")
    suspend fun loginWithFirebase(
        @Body firebaseLoginRequest: FirebaseLoginRequest
    ): Response<LoginResponse>

    @POST("auth/refresh-token")
    suspend fun refreshToken(
        @Body refreshRequest: RefreshTokenRequest
    ): Response<RefreshTokenDto>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body forgotPasswordRequest: ForgotPasswordRequest
    ): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Response<Unit>
}

// Request DTOs
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val firstName: String,
    val lastName: String
)

data class GoogleLoginRequest(
    val googleToken: String
)

data class FirebaseLoginRequest(
    val firebaseToken: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

// Response DTOs
data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val accountResponseDTO: AccountDto
) 
