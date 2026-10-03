package com.vizenture.forexwatch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.vizenture.forexwatch.common.TimeProvider
import com.vizenture.forexwatch.data.mapper.ForexChartMapper
import com.vizenture.forexwatch.data.mapper.ForexSearchResultsMapper
import com.vizenture.forexwatch.data.mapper.MarketStatsMapper
import com.vizenture.forexwatch.data.mapper.TrendingForexPairMapper
import com.vizenture.forexwatch.data.repository.chart.ForexPairChartRepository
import com.vizenture.forexwatch.data.repository.chart.ForexPairChartRepositoryImpl
import com.vizenture.forexwatch.data.repository.pair.ForexPairRepository
import com.vizenture.forexwatch.data.repository.pair.ForexPairRepositoryImpl
import com.vizenture.forexwatch.data.repository.details.ForexPairDetailsRepository
import com.vizenture.forexwatch.data.repository.details.ForexPairDetailsRepositoryImpl
import com.vizenture.forexwatch.data.repository.favouritePair.FavouriteForexPairRepository
import com.vizenture.forexwatch.data.repository.favouritePair.FavouriteForexPairRepositoryImpl
import com.vizenture.forexwatch.data.repository.favouriteSymbol.FavouriteForexSymbolRepository
import com.vizenture.forexwatch.data.repository.favouriteSymbol.FavouriteForexSymbolRepositoryImpl
import com.vizenture.forexwatch.data.repository.marketStats.MarketStatsRepository
import com.vizenture.forexwatch.data.repository.marketStats.MarketStatsRepositoryImpl
import com.vizenture.forexwatch.data.repository.reviewPrompt.ReviewPromptRepository
import com.vizenture.forexwatch.data.repository.reviewPrompt.ReviewPromptRepositoryImpl
import com.vizenture.forexwatch.data.repository.searchResults.ForexSearchResultsRepository
import com.vizenture.forexwatch.data.repository.searchResults.ForexSearchResultsRepositoryImpl
import com.vizenture.forexwatch.data.repository.trendingPairs.TrendingForexPairsRepository
import com.vizenture.forexwatch.data.repository.trendingPairs.TrendingForexPairsRepositoryImpl
import com.vizenture.forexwatch.data.source.local.database.ForexLocalDataSource
import com.vizenture.forexwatch.data.source.local.datastore.reviewanalytics.ReviewAnalyticsLocalDataSource
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // --- NEW FOREX BINDINGS ---
    @Provides @Singleton
    fun provideForexPairRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexLocalDataSource: ForexLocalDataSource,
        forexChartMapper: ForexChartMapper,
    ): ForexPairRepository = ForexPairRepositoryImpl(forexNetworkDataSource, forexLocalDataSource, forexChartMapper)

    @Provides @Singleton
    fun provideFavouriteForexPairRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexLocalDataSource: ForexLocalDataSource,
        forexChartMapper: ForexChartMapper,
    ): FavouriteForexPairRepository = FavouriteForexPairRepositoryImpl(forexNetworkDataSource, forexLocalDataSource, forexChartMapper)

    @Provides @Singleton
    fun provideFavouriteForexSymbolRepository(
        forexLocalDataSource: ForexLocalDataSource,
    ): FavouriteForexSymbolRepository = FavouriteForexSymbolRepositoryImpl(forexLocalDataSource)

    @Provides @Singleton
    fun provideForexPairDetailsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexChartMapper: ForexChartMapper,
    ): ForexPairDetailsRepository = ForexPairDetailsRepositoryImpl(forexNetworkDataSource, forexChartMapper)

    @Provides @Singleton
    fun provideForexPairChartRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexChartMapper: ForexChartMapper,
    ): ForexPairChartRepository = ForexPairChartRepositoryImpl(forexNetworkDataSource, forexChartMapper)

    // --- LEGACY BINDINGS (Required to keep Hilt happy for old ViewModels) ---
    @Provides @Singleton
    fun provideForexSearchResultsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexSearchResultsMapper: ForexSearchResultsMapper,
    ): ForexSearchResultsRepository = ForexSearchResultsRepositoryImpl(forexNetworkDataSource, forexSearchResultsMapper)

    @Provides @Singleton
    fun providesMarketStatsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        marketStatsMapper: MarketStatsMapper,
    ): MarketStatsRepository = MarketStatsRepositoryImpl(forexNetworkDataSource, marketStatsMapper)

    @Provides @Singleton
    fun provideTrendingForexPairsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        trendingForexPairMapper: TrendingForexPairMapper,
    ): TrendingForexPairsRepository = TrendingForexPairsRepositoryImpl(forexNetworkDataSource, trendingForexPairMapper)

    @Provides @Singleton
    fun provideReviewPromptRepository(
        reviewAnalyticsLocalDataSource: ReviewAnalyticsLocalDataSource,
        timeProvider: TimeProvider,
    ): ReviewPromptRepository = ReviewPromptRepositoryImpl(reviewAnalyticsLocalDataSource, timeProvider)
}
