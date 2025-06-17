package com.vidz.data.di

import com.vidz.data.repository.AuthRepositoryImpl
import com.vidz.data.repository.TokenRefreshRepositoryImpl
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.TokenRefreshRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository


    @Binds
    @Singleton
    abstract fun bindTokenRefreshRepository(
        tokenRefreshRepositoryImpl: TokenRefreshRepositoryImpl
    ): TokenRefreshRepository
}
