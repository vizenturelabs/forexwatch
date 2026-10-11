package com.vizenture.forexwatch.data.repository.pair

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

                // FIX: Use the helper to get the correct standard pair (e.g., USD/JPY)
                val tdSymbol = Constants.getStandardForexSymbol(fromSymbol, Constants.BASE_CURRENCY)

                val response = forexNetworkDataSource.getQuote(tdSymbol)
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
                }
            }

            forexLocalDataSource.updateForexPairs(terminalId, pairs)
            Result.Success(pairs)
        } catch (e: Exception) {
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
                // FIX: Included e.message to utilize the caught exception
                emit(Result.Error("Failed to load cached pairs: ${e.message}"))
            }
        }
    }

    override suspend fun updateCachedForexPairs(terminalId: Int, pairs: List<CurrencyPair>) {
        forexLocalDataSource.updateForexPairs(terminalId, pairs)
    }
}
