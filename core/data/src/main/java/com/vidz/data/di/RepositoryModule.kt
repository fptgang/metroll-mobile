package com.vidz.data.di

import com.vidz.data.repository.AccountManagementRepositoryImpl
import com.vidz.data.repository.AuthRepositoryImpl
import com.vidz.data.repository.CartRepositoryImpl
import com.vidz.data.repository.MetroLineRepositoryImpl
import com.vidz.data.repository.OrderRepositoryImpl
import com.vidz.data.repository.P2PJourneyRepositoryImpl
import com.vidz.data.repository.PaymentRepositoryImpl
import com.vidz.data.repository.StationRepositoryImpl
import com.vidz.data.repository.TicketRepositoryImpl
import com.vidz.data.repository.TimedTicketPlanRepositoryImpl
import com.vidz.data.repository.UserRepositoryImpl
import com.vidz.domain.repository.AccountManagementRepository
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.CartRepository
import com.vidz.domain.repository.MetroLineRepository
import com.vidz.domain.repository.OrderRepository
import com.vidz.domain.repository.P2PJourneyRepository
import com.vidz.domain.repository.PaymentRepository
import com.vidz.domain.repository.StationRepository
import com.vidz.domain.repository.TicketRepository
import com.vidz.domain.repository.TimedTicketPlanRepository
import com.vidz.domain.repository.UserRepository
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
    abstract fun bindAccountManagementRepository(
        accountManagementRepositoryImpl: AccountManagementRepositoryImpl
    ): AccountManagementRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindMetroLineRepository(
        metroLineRepositoryImpl: MetroLineRepositoryImpl
    ): MetroLineRepository

    @Binds
    @Singleton
    abstract fun bindStationRepository(
        stationRepositoryImpl: StationRepositoryImpl
    ): StationRepository

    @Binds
    @Singleton
    abstract fun bindTimedTicketPlanRepository(
        timedTicketPlanRepositoryImpl: TimedTicketPlanRepositoryImpl
    ): TimedTicketPlanRepository

    @Binds
    @Singleton
    abstract fun bindP2PJourneyRepository(
        p2pJourneyRepositoryImpl: P2PJourneyRepositoryImpl
    ): P2PJourneyRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindTicketRepository(
        ticketRepositoryImpl: TicketRepositoryImpl
    ): TicketRepository
}
