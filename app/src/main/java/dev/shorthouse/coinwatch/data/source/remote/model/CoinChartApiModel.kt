package com.vizenture.forexwatch.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class ForexChartApiModel(
    @SerializedName("candles") val candles: List<CandleApiModel>
)

data class CandleApiModel(
    @SerializedName("time") val time: String,
    @SerializedName("mid") val mid: OhlcApiModel
)

data class OhlcApiModel(
    @SerializedName("o") val open: String,
    @SerializedName("h") val high: String,
    @SerializedName("l") val low: String,
    @SerializedName("c") val close: String
)

