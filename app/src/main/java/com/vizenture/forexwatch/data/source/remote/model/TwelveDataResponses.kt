package com.vizenture.forexwatch.data.source.remote.model

import com.google.gson.annotations.SerializedName

// 1. Quote (Used for Market Screen & Details current price)
data class TwelveDataQuoteResponse(
    val symbol: String?,
    @SerializedName("instrument_name") val instrumentName: String?,
    val open: String?,
    val high: String?,
    val low: String?,
    val close: String?,
    @SerializedName("previous_close") val previousClose: String?,
    val change: String?,
    @SerializedName("percent_change") val percentChange: String?, // e.g., "-0.270%"
    val datetime: String?,
    val timestamp: Long?,
    val status: String?,
    val message: String?
)

// 2. Time Series (Used for Charts)
data class TwelveDataTimeSeriesResponse(
    val meta: TwelveDataMeta?,
    val values: List<TwelveDataCandle>?,
    val status: String?,
    val message: String? // Error message if status != "ok"
)

data class TwelveDataMeta(
    val symbol: String?,
    val interval: String?,
    @SerializedName("currency_base") val currencyBase: String?,
    @SerializedName("currency_quote") val currencyQuote: String?
)

data class TwelveDataCandle(
    val datetime: String, // "2023-10-26" or "2023-10-26 14:00:00"
    val open: String,
    val high: String,
    val low: String,
    val close: String
)

// 3. Search
data class TwelveDataSearchResponse(
    val data: List<TwelveDataSearchResult>?,
    val status: String?,
    val message: String?
)

data class TwelveDataSearchResult(
    val symbol: String,
    @SerializedName("instrument_name") val instrumentName: String,
    val exchange: String?,
    @SerializedName("instrument_type") val instrumentType: String?
)
