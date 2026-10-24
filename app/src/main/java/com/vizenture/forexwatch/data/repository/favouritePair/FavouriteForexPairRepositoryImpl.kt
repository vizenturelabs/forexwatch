package com.vizenture.forexwatch.data.repository.favouritePair

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.ForexLocalDataSource
import com.vizenture.forexwatch.data.source.local.database.dao.CachedChartDao
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.ui.model.ChartPeriod
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class FavouriteForexPairRepositoryImpl @Inject constructor(
    private val forexLocalDataSource: ForexLocalDataSource,
    private val cachedChartDao: CachedChartDao
) : FavouriteForexPairRepository {

    override fun getFavouriteForexPairs(terminalId: Int): Flow<Result<List<FavouriteCurrencyPair>>> {
        return flow {
            emit(Result.Loading)
            try {
                combine(
                    forexLocalDataSource.getFavouriteSymbols(terminalId),
                    forexLocalDataSource.getForexPairs(terminalId)
                ) { favouriteSymbols, allPairs ->
                    val favouriteSymbolsSet = favouriteSymbols.map { it.symbol }.toSet()

                    allPairs
                        .filter { it.symbol in favouriteSymbolsSet }
                        .map { currencyPair ->
                            // Fetch the 24-hour cached chart for the sparkline
                            val cachedChart = try {
                                cachedChartDao.getCachedChart(
                                    symbol = currencyPair.symbol,
                                    period = ChartPeriod.Day.stringName, // "24h"
                                    terminalId = terminalId
                                )
                            } catch (e: Exception) {
                                Timber.w(e, "Failed to fetch cached chart for ${currencyPair.symbol}")
                                null
                            }

                            val history24h = cachedChart?.data?.map { it.close }?.toImmutableList()
                                ?: persistentListOf()

                            FavouriteCurrencyPair(
                                symbol = currencyPair.symbol,
                                terminalId = currencyPair.terminalId,
                                name = currencyPair.name,
                                currentPrice = currencyPair.currentPrice,
                                bid = currencyPair.bid,
                                ask = currencyPair.ask,
                                spread = currencyPair.spread,
                                priceChangePercentage24h = currencyPair.priceChangePercentage24h,
                                priceHistory24h = history24h,
                                isJpyPair = currencyPair.isJpyPair
                            )
                        }
                }.collect { favouritePairs ->
                    emit(Result.Success(favouritePairs))
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load favourite pairs")
                emit(Result.Error("Failed to load favourite pairs"))
            }
        }
    }
}
