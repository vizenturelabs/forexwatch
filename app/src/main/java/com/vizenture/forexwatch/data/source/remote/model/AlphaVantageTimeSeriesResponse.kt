package com.vizenture.forexwatch.data.source.remote.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class AlphaVantageTimeSeriesResponse(
    @SerializedName("Meta Data")
    val metaData: TimeSeriesMetaData?,

    // Alpha Vantage uses dynamic keys here (e.g., "Time Series FX (60min)" or "Time Series FX (Daily)").
    // We use JsonObject to capture the entire response and parse the specific key in the Mapper.
    val timeSeriesData: JsonObject?
)

data class TimeSeriesMetaData(
    @SerializedName("1. Information") val information: String,
    @SerializedName("2. From Symbol") val fromSymbol: String,
    @SerializedName("3. To Symbol") val toSymbol: String,
    @SerializedName("4. Last Refreshed") val lastRefreshed: String,
    @SerializedName("5. Interval") val interval: String
)
