package com.vizenture.forexwatch.data.source.local.database.model

import androidx.room.Entity
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.model.Price
import java.math.BigDecimal
import kotlinx.collections.immutable.ImmutableList

@Entity(
    tableName = "favourite_currency_pairs",
    primaryKeys = ["symbol", "terminalId"]
)
data class FavouriteCurrencyPair(
    val symbol: String,
    val terminalId: Int,
    val name: String,
    val currentPrice: Price,
    val bid: Price,
    val ask: Price,
    val spread: Double,
    val priceChangePercentage24h: Percentage,
    val priceHistory24h: ImmutableList<BigDecimal>, // Used for sparkline/mini-chart
    val isJpyPair: Boolean = false
)
