package com.vizenture.forexwatch.data.source.remote.model

import com.google.gson.annotations.SerializedName

// 1. Quote (Used for Market Screen & Details current price)
data class TwelveDataQuoteResponse(
    val symbol: String?,
    val name: String?,
    @SerializedName("instrument_name") val instrumentName: String?,
    val open: String?,
    val high: String?,
    val low: String?,
    val close: String?,
    @SerializedName("previous_close") val previousClose: String?,
    val change: String?,
    @SerializedName("percent_change") val percentChange: String?,
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
    val message: String?
)

data class TwelveDataMeta(
    val symbol: String?,
    val interval: String?,
    @SerializedName("currency_base") val currencyBase: String?,
    @SerializedName("currency_quote") val currencyQuote: String?
)

data class TwelveDataCandle(
    val datetime: String,
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

// 4. Technical Indicators (Used for Chart Overlays)
data class TwelveDataIndicatorResponse(
    val values: List<IndicatorValue>?,
    val status: String?,
    val message: String?
)

// FIX: Twelve Data returns indicator-specific field names (sma, ema, rsi),
// NOT a generic "value" field. Added all possible fields and a computed
// property that returns whichever one is present.
data class IndicatorValue(
    val datetime: String,
    val sma: String?,
    val ema: String?,
    val rsi: String?
) {
    val value: String?
        get() = sma ?: ema ?: rsi
}
