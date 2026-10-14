package com.vizenture.forexwatch.data.repository.pair

import com.vizenture.forexwatch.common.Constants
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.mapper.ForexChartMapper
import com.vizenture.forexwatch.data.source.local.database.ForexLocalDataSource
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.model.Percentage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class ForexPairRepositoryImpl @Inject constructor(
    private val forexNetworkDataSource: ForexNetworkDataSource,
    private val forexLocalDataSource: ForexLocalDataSource,
    private val forexChartMapper: ForexChartMapper,
) : ForexPairRepository {

    override suspend fun getRemoteForexPairs(
        terminalId: Int,
        category: MarketCategory,
        currency: Currency,
    ): Result<List<CurrencyPair>> {
        return try {
            val symbolsToFetch = when (category) {
                MarketCategory.Major -> Constants.MAJOR_PAIRS.mapNotNull { fromSymbol ->
                    if (fromSymbol == Constants.BASE_CURRENCY) null else Constants.getStandardForexSymbol(fromSymbol, Constants.BASE_CURRENCY)
                }
                MarketCategory.Minor -> Constants.MINOR_PAIRS
                MarketCategory.Exotic -> Constants.EXOTIC_PAIRS
            }

            val pairs = symbolsToFetch.mapNotNull { tdSymbol ->
                try {
                    val response = forexNetworkDataSource.getQuote(tdSymbol)

                    if (response.status == "error") {
                        Timber.tag("ForexPairRepo").e("API Error for $tdSymbol: ${response.message}")
                    }

                    forexChartMapper.mapQuoteToPairDetails(response, currency)?.let { details ->
                        CurrencyPair(
                            symbol = details.symbol,
                            terminalId = terminalId,
                            name = details.name,
                            currentPrice = details.currentPrice,
                            bid = details.bid,
                            ask = details.ask,
                            spread = details.spread,
                            priceChangePercentage24h = Percentage(response.percentChange?.replace("%", "") ?: "0.00"),
                            isJpyPair = details.isJpyPair
                        )
                    } ?: run {
                        Timber.tag("ForexPairRepo").e(
                            "Mapper returned null for $tdSymbol. Close: ${response.close}, Symbol: ${response.symbol}, Status: ${response.status}"
                        )
                        null
                    }
                } catch (e: Exception) {
                    Timber.tag("ForexPairRepo").e(e, "Exception fetching $tdSymbol")
                    null
                }
            }

            Timber.tag("ForexPairRepo").d("Successfully mapped ${pairs.size} pairs out of ${symbolsToFetch.size}")
            forexLocalDataSource.updateForexPairs(terminalId, pairs)
            Result.Success(pairs)
        } catch (e: Exception) {
            Timber.tag("ForexPairRepo").e(e, "Critical error fetching pairs")
            Result.Error("Failed to fetch forex pairs: ${e.message}")
        }
    }

    override fun getCachedForexPairs(terminalId: Int): Flow<Result<List<CurrencyPair>>> {
        return flow {
            emit(Result.Loading)
            try {
                forexLocalDataSource.getForexPairs(terminalId).collect { pairs ->
                    emit(Result.Success(pairs))
                }
            } catch (e: Exception) {
                Timber.tag("ForexPairRepo").e(e, "Failed to load cached pairs")
                emit(Result.Error("Failed to load cached pairs: ${e.message}"))
            }
        }
    }

    override suspend fun updateCachedForexPairs(terminalId: Int, pairs: List<CurrencyPair>) {
        forexLocalDataSource.updateForexPairs(terminalId, pairs)
    }
}
