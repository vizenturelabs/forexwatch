package com.vizenture.forexwatch.di

import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindForexNetworkDataSource(
        impl: ForexNetworkDataSourceImpl
    ): ForexNetworkDataSource
}
