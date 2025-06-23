package com.vidz.data.di

// import com.vidz.metroll.core.data.BuildConfig // TODO: Add proper BuildConfig
import com.vidz.data.server.retrofit.AuthInterceptor
import com.vidz.domain.usecase.auth.GetFirebaseTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    @Named("AuthInterceptor")
    fun provideAuthInterceptor(
        getFirebaseTokenUseCase: GetFirebaseTokenUseCase
    ): Interceptor = AuthInterceptor(
        getFirebaseTokenUseCase = getFirebaseTokenUseCase
    )

}
