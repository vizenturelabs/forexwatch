package com.vizenture.forexwatch.di

import com.vizenture.forexwatch.common.SystemTimeProvider
import com.vizenture.forexwatch.common.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimeProviderModule {

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider {
        return SystemTimeProvider()
    }
}
