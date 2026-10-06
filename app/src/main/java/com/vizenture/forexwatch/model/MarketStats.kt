package com.vizenture.forexwatch.model

data class MarketStats(
    val totalMarketCap: Price,
    val totalVolume: Price,
    val btcDominance: Percentage,
    val ethDominance: Percentage,
    val activeCryptocurrencies: Long,
    val marketCapChangePercentage24h: Percentage
)
