package com.vidz.data.di

import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.data.server.retrofit.api.AccountApi
import com.vidz.data.server.retrofit.api.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofitServer: RetrofitServer): AuthApi {
        return retrofitServer.authApi
    }

    @Provides
    @Singleton
    fun provideAccountApi(retrofitServer: RetrofitServer): AccountApi {
        return retrofitServer.accountApi
    }
}
