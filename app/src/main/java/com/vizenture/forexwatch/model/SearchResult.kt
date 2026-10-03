package com.vizenture.forexwatch.model

data class SearchResult(
    val symbol: String,
    val name: String,
    val type: String // e.g., "Forex", "Equity", "Crypto"
)
