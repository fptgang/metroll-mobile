package com.vidz.data.di

import com.vidz.data.repository.AccountManagementRepositoryImpl
import com.vidz.data.repository.AuthRepositoryImpl
import com.vidz.data.repository.OrderRepositoryImpl
import com.vidz.data.repository.PaymentRepositoryImpl
import com.vidz.data.repository.UserRepositoryImpl
import com.vidz.domain.repository.AccountManagementRepository
import com.vidz.domain.repository.AuthRepository
import com.vidz.domain.repository.OrderRepository
import com.vidz.domain.repository.PaymentRepository
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
}
