package com.vidz.data.server.retrofit

import com.vidz.domain.usecase.auth.GetFirebaseTokenUseCase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val getFirebaseTokenUseCase: GetFirebaseTokenUseCase
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth endpoints
        if (shouldSkipAuth(originalRequest)) {
            return chain.proceed(originalRequest)
        }

        // Get Firebase token and add to request with timeout
        val firebaseToken = runBlocking {
            withTimeoutOrNull(10000) { // 10 second timeout
                try {
                    getFirebaseTokenUseCase(forceRefresh = false)
                } catch (e: Exception) {
                    null // Return null on any exception
                }
            }
        }

        val requestWithToken = if (firebaseToken != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $firebaseToken")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(requestWithToken)

        // Handle 401 Unauthorized - try to refresh Firebase token
        if (response.code == 401 && !shouldSkipAuth(originalRequest)) {
            response.close()
            
            val refreshedFirebaseToken = runBlocking {
                withTimeoutOrNull(15000) { // 15 second timeout for refresh
                    try {
                        getFirebaseTokenUseCase(forceRefresh = true)
                    } catch (e: Exception) {
                        null // Return null on any exception
                    }
                }
            }

            return if (refreshedFirebaseToken != null) {
                // Retry the original request with refreshed Firebase token
                val newRequestWithToken = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $refreshedFirebaseToken")
                    .build()
                chain.proceed(newRequestWithToken)
            } else {
                // Refresh failed, proceed without auth
                chain.proceed(originalRequest)
            }
        }

        return response
    }

    private fun shouldSkipAuth(request: Request): Boolean {
        val url = request.url.toString()
        return url.contains("/accounts/login") ||
               url.contains("/auth/register") || 
               url.contains("/auth/refresh-token") ||
               url.contains("/auth/forgot-password") ||
               url.contains("/auth/reset-password")
    }
} 
