package com.vidz.data.server.retrofit

import com.vidz.data.server.retrofit.api.AccountApi
import com.vidz.data.server.retrofit.api.AuthApi
import com.vidz.data.server.retrofit.api.TokenRefreshApi
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitServer @Inject constructor(private val retrofit: Retrofit) {
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val accountApi: AccountApi by lazy { retrofit.create(AccountApi::class.java) }
    val tokenRefreshApi: TokenRefreshApi by lazy { retrofit.create(TokenRefreshApi::class.java) }
}
