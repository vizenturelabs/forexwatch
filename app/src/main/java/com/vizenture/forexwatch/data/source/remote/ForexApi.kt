package com.vizenture.forexwatch.data.source.remote

import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageExchangeRateResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageTimeSeriesResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ForexApi {

    // 1. Real-time Exchange Rate (Used for Bid/Ask/Current Price)
    @GET("query")
    suspend fun getExchangeRate(
        @Query("function") function: String = "CURRENCY_EXCHANGE_RATE",
        @Query("from_symbol") fromSymbol: String,
        @Query("to_symbol") toSymbol: String
    ): Response<AlphaVantageExchangeRateResponse>

    // 2. Intraday OHLC (Used for 1H, 1D charts)
    @GET("query")
    suspend fun getForexIntraday(
        @Query("function") function: String = "FX_INTRADAY",
        @Query("from_symbol") fromSymbol: String,
        @Query("to_symbol") toSymbol: String,
        @Query("interval") interval: String = "60min", // 1min, 5min, 15min, 30min, 60min
        @Query("outputsize") outputsize: String = "compact" // compact = last 100 data points
    ): Response<AlphaVantageTimeSeriesResponse>

    // 3. Daily OHLC (Used for 1W, 1M, 1Y charts)
    @GET("query")
    suspend fun getForexDaily(
        @Query("function") function: String = "FX_DAILY",
        @Query("from_symbol") fromSymbol: String,
        @Query("to_symbol") toSymbol: String,
        @Query("outputsize") outputsize: String = "compact"
    ): Response<AlphaVantageTimeSeriesResponse>

    // 4. Symbol Search (Used for the Search Screen)
    @GET("query")
    suspend fun searchSymbols(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String
    ): Response<AlphaVantageSearchResponse>
}
