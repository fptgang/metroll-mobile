package com.vidz.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.vidz.data.repository.FirebaseTicketRepositoryImpl
import com.vidz.domain.repository.FirebaseTicketRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {
    
    @Binds
    abstract fun bindFirebaseTicketRepository(
        firebaseTicketRepositoryImpl: FirebaseTicketRepositoryImpl
    ): FirebaseTicketRepository
    
    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
        
        @Provides
        @Singleton
        fun provideFirebaseDatabase(): FirebaseDatabase {
            return FirebaseDatabase.getInstance("https://metroll-bbda2-default-rtdb.asia-southeast1.firebasedatabase.app/")
        }
    }
} 