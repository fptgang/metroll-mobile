package com.vidz.routemanagement.di

import com.vidz.routemanagement.domain.usecase.GetMetroLinesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.vidz.domain.repository.MetroLineRepository
import com.vidz.domain.repository.StationRepository
import com.vidz.domain.repository.P2PJourneyRepository
import com.vidz.domain.usecase.station.GetStationsUseCase
import com.vidz.domain.usecase.p2pjourney.GetP2PJourneyByStationsUseCase
import com.vidz.domain.usecase.cart.AddToCartUseCase
import com.vidz.domain.usecase.cart.GetCartItemsUseCase
import com.vidz.domain.repository.CartRepository
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

    @Provides
    @Singleton
    fun provideGetStationsUseCase(
        repository: StationRepository
    ): GetStationsUseCase {
        return GetStationsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetP2PJourneyByStationsUseCase(
        repository: P2PJourneyRepository
    ): GetP2PJourneyByStationsUseCase {
        return GetP2PJourneyByStationsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddToCartUseCase(
        repository: CartRepository
    ): AddToCartUseCase {
        return AddToCartUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCartItemsUseCase(
        repository: CartRepository
    ): GetCartItemsUseCase {
        return GetCartItemsUseCase(repository)
    }
} 