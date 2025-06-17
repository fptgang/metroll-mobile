package com.vidz.metrollapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {

    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO // Adjust the parallelism level as needed
    }
}
