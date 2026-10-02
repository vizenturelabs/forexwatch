package com.vizenture.forexwatch.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class AlphaVantageExchangeRateResponse(
    @SerializedName("Realtime Currency Exchange Rate")
    val exchangeRate: ExchangeRateDetails?
)

data class ExchangeRateDetails(
    @SerializedName("1. From_Currency Code") val fromSymbol: String,
    @SerializedName("2. From_Currency Name") val fromName: String,
    @SerializedName("3. To_Currency Code") val toSymbol: String,
    @SerializedName("4. To_Currency Name") val toName: String,
    @SerializedName("5. Exchange Rate") val rate: String,
    @SerializedName("6. Last Refreshed") val lastRefreshed: String,
    @SerializedName("8. Bid Price") val bidPrice: String,
    @SerializedName("9. Ask Price") val askPrice: String
)
