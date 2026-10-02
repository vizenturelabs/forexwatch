package com.vizenture.forexwatch.data.source.remote

import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageExchangeRateResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageTimeSeriesResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageSearchResponse

interface ForexNetworkDataSource {
    suspend fun getExchangeRate(fromSymbol: String, toSymbol: String): AlphaVantageExchangeRateResponse
    suspend fun getForexIntraday(fromSymbol: String, toSymbol: String, interval: String): AlphaVantageTimeSeriesResponse
    suspend fun getForexDaily(fromSymbol: String, toSymbol: String): AlphaVantageTimeSeriesResponse
    suspend fun searchSymbols(keywords: String): AlphaVantageSearchResponse
}
