package com.vizenture.forexwatch.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vizenture.forexwatch.data.source.local.database.model.CachedChart

// FIX: Added @Suppress("unused") to silence the IDE warnings.
// Room uses these methods via annotation processing, so they appear unused to the compiler.
@Suppress("unused")
@Dao
interface CachedChartDao {
    @Query("SELECT * FROM cached_charts WHERE symbol = :symbol AND period = :period AND terminalId = :terminalId")
    suspend fun getCachedChart(symbol: String, period: String, terminalId: Int): CachedChart?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedChart(chart: CachedChart)
}
