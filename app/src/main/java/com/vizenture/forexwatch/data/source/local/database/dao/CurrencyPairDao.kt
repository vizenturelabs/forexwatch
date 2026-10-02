package com.vizenture.forexwatch.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyPairDao {

    @Query("SELECT * FROM currency_pairs WHERE terminalId = :terminalId")
    fun getForexPairs(terminalId: Int): Flow<List<CurrencyPair>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForexPairs(pairs: List<CurrencyPair>)

    @Query("DELETE FROM currency_pairs WHERE terminalId = :terminalId")
    fun deleteAllForexPairs(terminalId: Int)

    @Transaction
    fun updateForexPairs(terminalId: Int, pairs: List<CurrencyPair>) {
        deleteAllForexPairs(terminalId)
        insertForexPairs(pairs)
    }
}
