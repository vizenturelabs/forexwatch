package com.vizenture.forexwatch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.vizenture.forexwatch.common.TimeProvider
import com.vizenture.forexwatch.data.mapper.ForexPairChartMapper
import com.vizenture.forexwatch.data.mapper.ForexPairDetailsMapper
import com.vizenture.forexwatch.data.mapper.ForexPairMapper
import com.vizenture.forexwatch.data.mapper.ForexSearchResultsMapper
import com.vizenture.forexwatch.data.mapper.FavouriteForexPairMapper
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
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideForexPairRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexLocalDataSource: ForexLocalDataSource,
        forexPairMapper: ForexPairMapper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ForexPairRepository {
        return ForexPairRepositoryImpl(
            forexNetworkDataSource = forexNetworkDataSource,
            forexLocalDataSource = forexLocalDataSource,
            forexPairMapper = forexPairMapper,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideFavouriteForexPairRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexLocalDataSource: ForexLocalDataSource,
        favouriteForexPairMapper: FavouriteForexPairMapper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): FavouriteForexPairRepository {
        return FavouriteForexPairRepositoryImpl(
            forexNetworkDataSource = forexNetworkDataSource,
            forexLocalDataSource = forexLocalDataSource,
            favouriteForexPairMapper = favouriteForexPairMapper,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideFavouriteForexSymbolRepository(
        forexLocalDataSource: ForexLocalDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): FavouriteForexSymbolRepository {
        return FavouriteForexSymbolRepositoryImpl(
            forexLocalDataSource = forexLocalDataSource,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideForexPairDetailsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexPairDetailsMapper: ForexPairDetailsMapper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ForexPairDetailsRepository {
        return ForexPairDetailsRepositoryImpl(
            forexNetworkDataSource = forexNetworkDataSource,
            forexPairDetailsMapper = forexPairDetailsMapper,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideForexPairChartRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexPairChartMapper: ForexPairChartMapper,
        timeProvider: TimeProvider,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ForexPairChartRepository {
        return ForexPairChartRepositoryImpl(
            forexNetworkDataSource = forexNetworkDataSource,
            forexPairChartMapper = forexPairChartMapper,
            timeProvider = timeProvider,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun provideForexSearchResultsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        forexSearchResultsMapper: ForexSearchResultsMapper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ForexSearchResultsRepository {
        return ForexSearchResultsRepositoryImpl(
            forexNetworkDataSource = forexNetworkDataSource,
            forexSearchResultsMapper = forexSearchResultsMapper,
            ioDispatcher = ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun providesMarketStatsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        marketStatsMapper: MarketStatsMapper,
    ): MarketStatsRepository {
        return MarketStatsRepositoryImpl(
            forexNetworkDataSource = forexNetworkDataSource,
            marketStatsMapper = marketStatsMapper
        )
    }

    @Provides
    @Singleton
    fun provideTrendingForexPairsRepository(
        forexNetworkDataSource: ForexNetworkDataSource,
        trendingForexPairMapper: TrendingForexPairMapper,
    ): TrendingForexPairsRepository {
        return TrendingForexPairsRepositoryImpl(
            forexNetworkDataSource = forexNetworkDataSource,
            trendingForexPairMapper = trendingForexPairMapper
        )
    }

    @Provides
    @Singleton
    fun provideReviewPromptRepository(
        reviewAnalyticsLocalDataSource: ReviewAnalyticsLocalDataSource,
        timeProvider: TimeProvider,
    ): ReviewPromptRepository {
        return ReviewPromptRepositoryImpl(
            reviewAnalyticsLocalDataSource = reviewAnalyticsLocalDataSource,
            timeProvider = timeProvider,
        )
    }
}
