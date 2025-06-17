package com.vidz.data.server.retrofit

import com.vidz.domain.Result.Success
import com.vidz.domain.repository.TokenRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth endpoints
        if (shouldSkipAuth(originalRequest)) {
            return chain.proceed(originalRequest)
        }

        // Get access token and add to request with timeout
        val accessToken = runBlocking {
            withTimeoutOrNull(10000) { // Increased to 10 second timeout
                try {
                    tokenRepository.getValidAccessToken().firstOrNull()
                } catch (e: Exception) {
                    null // Return null on any exception
                }
            }
        }

        val requestWithToken = when (accessToken) {
            is Success -> {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer ${accessToken.data}")
                    .build()
            }
            else -> originalRequest
        }

        val response = chain.proceed(requestWithToken)

        // Handle 401 Unauthorized - try to refresh token
        if (response.code == 401 && !shouldSkipAuth(originalRequest)) {
            response.close()
            
            val refreshResult = runBlocking {
                withTimeoutOrNull(15000) { // Increased to 15 second timeout for refresh
                    try {
                        tokenRepository.handleTokenExpired().firstOrNull()
                    } catch (e: Exception) {
                        null // Return null on any exception
                    }
                }
            }

            return when (refreshResult) {
                is Success -> {
                    // Retry the original request with new token
                    val newRequestWithToken = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer ${refreshResult.data}")
                        .build()
                    chain.proceed(newRequestWithToken)
                }
                else -> {
                    // Refresh failed, proceed without auth
                    chain.proceed(originalRequest)
                }
            }
        }

        return response
    }

    private fun shouldSkipAuth(request: Request): Boolean {
        val url = request.url.toString()
        return url.contains("/auth/login") || 
               url.contains("/auth/register") || 
               url.contains("/auth/refresh-token") ||
               url.contains("/auth/forgot-password") ||
               url.contains("/auth/reset-password")
    }
} 
