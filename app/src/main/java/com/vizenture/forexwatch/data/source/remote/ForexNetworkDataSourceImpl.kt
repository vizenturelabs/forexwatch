package com.vizenture.forexwatch.data.source.remote

import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageExchangeRateResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageSearchResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageTimeSeriesResponse
import javax.inject.Inject

class ForexNetworkDataSourceImpl @Inject constructor(
    private val forexApi: ForexApi
) : ForexNetworkDataSource {

    override suspend fun getExchangeRate(fromSymbol: String, toSymbol: String): AlphaVantageExchangeRateResponse {
        return forexApi.getExchangeRate(fromSymbol = fromSymbol, toSymbol = toSymbol).body()
            ?: throw Exception("Failed to fetch exchange rate")
    }

    override suspend fun getForexIntraday(fromSymbol: String, toSymbol: String, interval: String): AlphaVantageTimeSeriesResponse {
        return forexApi.getForexIntraday(fromSymbol = fromSymbol, toSymbol = toSymbol, interval = interval).body()
            ?: throw Exception("Failed to fetch intraday data")
    }

    override suspend fun getForexDaily(fromSymbol: String, toSymbol: String): AlphaVantageTimeSeriesResponse {
        return forexApi.getForexDaily(fromSymbol = fromSymbol, toSymbol = toSymbol).body()
            ?: throw Exception("Failed to fetch daily data")
    }

    override suspend fun searchSymbols(keywords: String): AlphaVantageSearchResponse {
        return forexApi.searchSymbols(keywords = keywords).body()
            ?: throw Exception("Failed to search symbols")
    }
}
