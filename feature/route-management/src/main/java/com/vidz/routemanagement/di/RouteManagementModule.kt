package com.vidz.routemanagement.di

import com.vidz.routemanagement.domain.usecase.GetMetroLinesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.vidz.domain.repository.MetroLineRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RouteManagementModule {

    @Provides
    @Singleton
    fun provideGetMetroLinesUseCase(
        repository: MetroLineRepository
    ): GetMetroLinesUseCase {
        return GetMetroLinesUseCase(repository)
    }
} 