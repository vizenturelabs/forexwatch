package com.vizenture.forexwatch.data.source.remote.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

@JsonAdapter(AlphaVantageTimeSeriesResponseAdapter::class)
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

/**
 * Custom Gson Deserializer.
 * Without this, Gson defaults to looking for a JSON key literally named "timeSeriesData".
 * Since Alpha Vantage uses dynamic root keys, this adapter maps the entire root JsonObject
 * into timeSeriesData so ForexChartMapper can successfully locate the time series block.
 */
class AlphaVantageTimeSeriesResponseAdapter : JsonDeserializer<AlphaVantageTimeSeriesResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AlphaVantageTimeSeriesResponse {
        val jsonObject = json?.asJsonObject ?: JsonObject()

        val metaData = jsonObject.getAsJsonObject("Meta Data")?.let {
            context?.deserialize<TimeSeriesMetaData>(it, TimeSeriesMetaData::class.java)
        }

        return AlphaVantageTimeSeriesResponse(
            metaData = metaData,
            timeSeriesData = jsonObject
        )
    }
}
