package com.vizenture.forexwatch.data.source.remote.model

import com.google.gson.annotations.SerializedName

data class AlphaVantageSearchResponse(
    @SerializedName("bestMatches")
    val bestMatches: List<SearchResult>?
)

data class SearchResult(
    @SerializedName("1. symbol") val symbol: String,
    @SerializedName("2. name") val name: String,
    @SerializedName("3. type") val type: String
)
