package com.vizenture.forexwatch.data.repository.favouritePair

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.ForexLocalDataSource
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class FavouriteForexPairRepositoryImpl @Inject constructor(
    private val forexLocalDataSource: ForexLocalDataSource
) : FavouriteForexPairRepository {

    override fun getFavouriteForexPairs(terminalId: Int): Flow<Result<List<FavouriteCurrencyPair>>> {
        return flow {
            emit(Result.Loading)
            try {
                // Combine favourite symbols with all cached currency pairs
                combine(
                    forexLocalDataSource.getFavouriteSymbols(terminalId),
                    forexLocalDataSource.getForexPairs(terminalId)
                ) { favouriteSymbols, allPairs ->
                    val favouriteSymbolsSet = favouriteSymbols.map { it.symbol }.toSet()

                    allPairs
                        .filter { it.symbol in favouriteSymbolsSet }
                        .map { currencyPair ->
                            FavouriteCurrencyPair(
                                symbol = currencyPair.symbol,
                                terminalId = currencyPair.terminalId,
                                name = currencyPair.name,
                                currentPrice = currencyPair.currentPrice,
                                bid = currencyPair.bid,
                                ask = currencyPair.ask,
                                spread = currencyPair.spread,
                                priceChangePercentage24h = currencyPair.priceChangePercentage24h,
                                priceHistory24h = persistentListOf(),
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
