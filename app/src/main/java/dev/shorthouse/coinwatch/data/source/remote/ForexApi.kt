package com.vizenture.forexwatch.data.source.remote

import com.vizenture.forexwatch.data.source.remote.model.ForexChartApiModel
import com.vizenture.forexwatch.data.source.remote.model.ForexDetailsApiModel
import com.vizenture.forexwatch.data.source.remote.model.ForexSearchResultsApiModel
import com.vizenture.forexwatch.data.source.remote.model.ForexPairsApiModel
import com.vizenture.forexwatch.data.source.remote.model.FavouriteForexPairsApiModel
import com.vizenture.forexwatch.data.source.remote.model.MarketStatsApiModel
import com.vizenture.forexwatch.data.source.remote.model.TrendingForexPairsApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ForexApi {
    
    @GET("forex/pairs")
    suspend fun getForexPairs(
        @Header("X-Terminal-Id") terminalId: String,
        @Query("fields") fields: String = "bid,ask,spread",
        @Query("orderBy") orderBy: String = "volume",
        @Query("timePeriod") timePeriod: String = "24h",
        @Query("orderDirection") orderDirection: String = "desc",
        @Query("limit") limit: String = "100"
    ): Response<ForexPairsApiModel>

    @GET("forex/pairs")
    suspend fun getFavouriteForexPairs(
        @Header("X-Terminal-Id") terminalId: String,
        @Query("symbols[]") symbols: List<String>,
        @Query("fields") fields: String = "bid,ask,spread",
        @Query("orderBy") orderBy: String = "volume",
        @Query("timePeriod") timePeriod: String = "24h",
        @Query("orderDirection") orderDirection: String = "desc",
        @Query("limit") limit: String = "100"
    ): Response<FavouriteForexPairsApiModel>

    @GET("forex/pairs/{symbol}")
    suspend fun getForexPairDetails(
        @Header("X-Terminal-Id") terminalId: String,
        @Path("symbol") symbol: String,
        @Query("fields") fields: String = "bid,ask,spread,open,high,low,close"
    ): Response<ForexDetailsApiModel>

    @GET("forex/pairs/{symbol}/history")
    suspend fun getForexPairChart(
        @Header("X-Terminal-Id") terminalId: String,
        @Path("symbol") symbol: String,
        @Query("timePeriod") chartPeriod: String = "24h",
        @Query("interval") interval: String = "1h",
        @Query("fields") fields: String = "open,high,low,close,volume"
    ): Response<ForexChartApiModel>

    @GET("forex/search")
    suspend fun getForexSearchResults(
        @Header("X-Terminal-Id") terminalId: String,
        @Query("query") searchQuery: String = ""
    ): Response<ForexSearchResultsApiModel>

    @GET("forex/stats")
    suspend fun getMarketStats(
        @Header("X-Terminal-Id") terminalId: String
    ): Response<MarketStatsApiModel>

    @GET("forex/pairs/trending")
    suspend fun getTrendingForexPairs(
        @Header("X-Terminal-Id") terminalId: String,
        @Query("timePeriod") timePeriod: String = "24h",
        @Query("limit") limit: String = "10"
    ): Response<TrendingForexPairsApiModel>
}
