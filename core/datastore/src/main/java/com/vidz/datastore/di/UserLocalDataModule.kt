package com.vidz.datastore.di

import com.vidz.datastore.repository.UserLocalRepositoryImpl
import com.vidz.domain.repository.UserLocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserLocalDataModule {

    @Binds
    @Singleton
    abstract fun bindUserLocalRepository(
        repositoryImpl: UserLocalRepositoryImpl
    ): UserLocalRepository
} 
