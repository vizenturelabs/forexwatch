package com.vizenture.forexwatch.data.source.local.database.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.vizenture.forexwatch.data.source.local.database.ChartTypeConverters
import com.vizenture.forexwatch.model.OhlcEntry

// FIX: Added @Suppress("unused") to silence the IDE warning.
// Room uses this class via annotation processing, so it appears unused to the compiler.
@Suppress("unused")
@Entity(
    tableName = "cached_charts",
    primaryKeys = ["symbol", "period", "terminalId"]
)
@TypeConverters(ChartTypeConverters::class)
data class CachedChart(
    val symbol: String,
    val period: String, // e.g., "1h", "1day"
    val terminalId: Int,
    val data: List<OhlcEntry>,
    val lastUpdated: Long
)
