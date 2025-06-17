package com.vidz.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
// import com.vidz.metroll.core.data.BuildConfig // TODO: Add proper BuildConfig
import com.vidz.data.server.retrofit.AuthInterceptor
import com.vidz.data.server.retrofit.api.TokenRefreshApi
import com.vidz.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    @Named("AuthInterceptor")
    fun provideAuthInterceptor(
        tokenRepository: TokenRepository
    ): Interceptor = AuthInterceptor(tokenRepository)

    @Provides
    @Singleton
    fun provideTokenRefreshApi(
        loggingInterceptor: HttpLoggingInterceptor
    ): TokenRefreshApi {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        
        // Create a separate OkHttpClient without AuthInterceptor to avoid dependency cycle
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
            
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.metroll.mock/") // Mock URL for development
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(TokenRefreshApi::class.java)
    }
}

 
