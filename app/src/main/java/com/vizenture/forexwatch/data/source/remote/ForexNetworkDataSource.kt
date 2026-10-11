package com.vizenture.forexwatch.data.source.remote

import com.google.gson.JsonObject
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageExchangeRateResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageSearchResponse

interface ForexNetworkDataSource {
    suspend fun getExchangeRate(fromSymbol: String, toSymbol: String): AlphaVantageExchangeRateResponse
    suspend fun getForexIntraday(fromSymbol: String, toSymbol: String, interval: String): JsonObject
    suspend fun getForexDaily(fromSymbol: String, toSymbol: String): JsonObject
    suspend fun searchSymbols(keywords: String): AlphaVantageSearchResponse
}
