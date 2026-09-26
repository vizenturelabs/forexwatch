package com.vizenture.forexwatch.model

data class ForexPairDetails(
    val symbol: String,
    val name: String,
    val description: String,
    val currentPrice: Price,
    val bid: Price,
    val ask: Price,
    val spread: Double,
    val open24h: Price,
    val high24h: Price,
    val low24h: Price,
    val volume24h: Double,
    val pipValue: Double,
    val isJpyPair: Boolean = false
)
