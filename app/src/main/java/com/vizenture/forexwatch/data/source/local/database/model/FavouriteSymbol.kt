package com.vizenture.forexwatch.data.source.local.database.model

import androidx.room.Entity

@Entity(
    tableName = "favourite_symbols",
    primaryKeys = ["symbol", "terminalId"]
)
data class FavouriteSymbol(
    val symbol: String,
    val terminalId: Int
)
