package com.vizenture.forexwatch.data.source.remote

import com.google.gson.JsonObject
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageExchangeRateResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ForexApi {

    @GET("query")
    suspend fun getExchangeRate(
        @Query("function") function: String = "CURRENCY_EXCHANGE_RATE",
        @Query("from_symbol") fromSymbol: String,
        @Query("to_symbol") toSymbol: String
    ): Response<AlphaVantageExchangeRateResponse>

    // Bypass @JsonAdapter bugs by returning raw JsonObject
    @GET("query")
    suspend fun getForexIntraday(
        @Query("function") function: String = "FX_INTRADAY",
        @Query("from_symbol") fromSymbol: String,
        @Query("to_symbol") toSymbol: String,
        @Query("interval") interval: String = "60min",
        @Query("outputsize") outputsize: String = "compact"
    ): Response<JsonObject>

    // Bypass @JsonAdapter bugs by returning raw JsonObject
    @GET("query")
    suspend fun getForexDaily(
        @Query("function") function: String = "FX_DAILY",
        @Query("from_symbol") fromSymbol: String,
        @Query("to_symbol") toSymbol: String,
        @Query("outputsize") outputsize: String = "compact"
    ): Response<JsonObject>

    @GET("query")
    suspend fun searchSymbols(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String
    ): Response<AlphaVantageSearchResponse>
}
