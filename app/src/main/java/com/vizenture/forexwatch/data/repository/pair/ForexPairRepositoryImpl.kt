package com.vizenture.forexwatch.data.repository.pair

import android.util.Log
import com.vizenture.forexwatch.common.Constants
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.mapper.ForexChartMapper
import com.vizenture.forexwatch.data.source.local.database.ForexLocalDataSource
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.model.Percentage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ForexPairRepositoryImpl @Inject constructor(
    private val forexNetworkDataSource: ForexNetworkDataSource,
    private val forexLocalDataSource: ForexLocalDataSource,
    private val forexChartMapper: ForexChartMapper,
) : ForexPairRepository {

    override suspend fun getRemoteForexPairs(
        terminalId: Int,
        pairSort: PairSort,
        currency: Currency,
    ): Result<List<CurrencyPair>> {
        return try {
            val pairs = Constants.MAJOR_PAIRS.mapNotNull { fromSymbol ->
                if (fromSymbol == Constants.BASE_CURRENCY) return@mapNotNull null

                val tdSymbol = Constants.getStandardForexSymbol(fromSymbol, Constants.BASE_CURRENCY)

                try {
                    val response = forexNetworkDataSource.getQuote(tdSymbol)

                    // DIAGNOSTIC: Check if Twelve Data returned an explicit error
                    if (response.status == "error") {
                        Log.e("ForexPairRepo", "API Error for $tdSymbol: ${response.message}")
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
                        // DIAGNOSTIC: Mapper returned null, log the raw response state
                        Log.e("ForexPairRepo", "Mapper returned null for $tdSymbol. Close: ${response.close}, Symbol: ${response.symbol}, Status: ${response.status}")
                        null
                    }
                } catch (e: Exception) {
                    // DIAGNOSTIC: Network or parsing exception
                    Log.e("ForexPairRepo", "Exception fetching $tdSymbol: ${e.message}")
                    null
                }
            }

            Log.d("ForexPairRepo", "Successfully mapped ${pairs.size} pairs out of ${Constants.MAJOR_PAIRS.size}")
            forexLocalDataSource.updateForexPairs(terminalId, pairs)
            Result.Success(pairs)
        } catch (e: Exception) {
            Log.e("ForexPairRepo", "Critical error fetching pairs: ${e.message}")
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
                Log.e("ForexPairRepo", "Failed to load cached pairs: ${e.message}")
                emit(Result.Error("Failed to load cached pairs: ${e.message}"))
            }
        }
    }

    override suspend fun updateCachedForexPairs(terminalId: Int, pairs: List<CurrencyPair>) {
        forexLocalDataSource.updateForexPairs(terminalId, pairs)
    }
}
