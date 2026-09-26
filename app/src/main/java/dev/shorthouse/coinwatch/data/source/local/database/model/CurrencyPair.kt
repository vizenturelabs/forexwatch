package com.vizenture.forexwatch.data.source.local.database.model

import androidx.room.Entity
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.model.Price

@Entity(
    tableName = "currency_pairs",
    primaryKeys = ["symbol", "terminalId"]
)
data class CurrencyPair(
    val symbol: String,
    val terminalId: Int,
    val name: String,
    val currentPrice: Price,
    val bid: Price,
    val ask: Price,
    val spread: Double,
    val priceChangePercentage24h: Percentage,
    val isJpyPair: Boolean = false
)
