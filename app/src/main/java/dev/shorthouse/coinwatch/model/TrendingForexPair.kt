package com.vizenture.forexwatch.model

import kotlinx.collections.immutable.ImmutableList
import java.math.BigDecimal

data class TrendingForexPair(
    val symbol: String,
    val name: String,
    val currentPrice: Price,
    val priceChangePercentage24h: Percentage,
    val sparkline: ImmutableList<BigDecimal>,
    val isJpyPair: Boolean = false
)
