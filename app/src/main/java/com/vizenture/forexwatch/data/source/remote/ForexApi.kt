package com.vizenture.forexwatch.data.source.remote

import com.vizenture.forexwatch.data.source.remote.model.TwelveDataQuoteResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataSearchResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataTimeSeriesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ForexApi {

    // 1. Quote (Current price, 24h high/low, percent change)
    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String // e.g., "EUR/USD"
    ): Response<TwelveDataQuoteResponse>

    // 2. Time Series (Charts)
    @GET("time_series")
    suspend fun getTimeSeries(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String, // 5min, 1h, 1day, etc.
        @Query("outputsize") outputsize: Int = 100
    ): Response<TwelveDataTimeSeriesResponse>

    // 3. Symbol Search
    @GET("symbol_search")
    suspend fun searchSymbols(
        @Query("symbol") keywords: String
    ): Response<TwelveDataSearchResponse>
}
