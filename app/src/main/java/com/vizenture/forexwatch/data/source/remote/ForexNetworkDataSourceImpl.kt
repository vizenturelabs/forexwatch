package com.vizenture.forexwatch.data.source.remote

import com.vizenture.forexwatch.data.source.remote.model.TwelveDataIndicatorResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataQuoteResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataSearchResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataTimeSeriesResponse
import javax.inject.Inject

class ForexNetworkDataSourceImpl @Inject constructor(
    private val forexApi: ForexApi
) : ForexNetworkDataSource {

    override suspend fun getQuote(symbol: String): TwelveDataQuoteResponse {
        return forexApi.getQuote(symbol = symbol).body()
            ?: throw Exception("Failed to fetch quote")
    }

    override suspend fun getTimeSeries(symbol: String, interval: String): TwelveDataTimeSeriesResponse {
        return forexApi.getTimeSeries(symbol = symbol, interval = interval).body()
            ?: throw Exception("Failed to fetch time series")
    }

    override suspend fun searchSymbols(keywords: String): TwelveDataSearchResponse {
        return forexApi.searchSymbols(keywords = keywords).body()
            ?: throw Exception("Failed to search symbols")
    }

    // FIX: Implemented the missing technical indicator network call
    override suspend fun getTechnicalIndicator(
        symbol: String,
        interval: String,
        endpoint: String,
        timePeriod: Int
    ): TwelveDataIndicatorResponse {
        return forexApi.getTechnicalIndicator(
            endpoint = endpoint,
            symbol = symbol,
            interval = interval,
            timePeriod = timePeriod
        ).body() ?: throw Exception("Failed to fetch technical indicator")
    }
}
