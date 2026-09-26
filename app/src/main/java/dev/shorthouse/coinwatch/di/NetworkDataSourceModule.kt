package com.vizenture.forexwatch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.vizenture.forexwatch.data.source.remote.ForexApi
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkDataSourceModule {

    @Provides
    @Singleton
    fun provideForexNetworkDataSource(forexApi: ForexApi): ForexNetworkDataSource {
        return ForexNetworkDataSourceImpl(forexApi = forexApi)
    }
}
