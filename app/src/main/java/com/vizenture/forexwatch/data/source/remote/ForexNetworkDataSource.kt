package com.vizenture.forexwatch.data.source.remote

import com.vizenture.forexwatch.data.source.remote.model.TwelveDataQuoteResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataSearchResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataTimeSeriesResponse

interface ForexNetworkDataSource {
    suspend fun getQuote(symbol: String): TwelveDataQuoteResponse
    suspend fun getTimeSeries(symbol: String, interval: String): TwelveDataTimeSeriesResponse
    suspend fun searchSymbols(keywords: String): TwelveDataSearchResponse
}
