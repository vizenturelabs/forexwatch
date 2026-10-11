package com.vizenture.forexwatch.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair

@Dao
interface CurrencyPairDao {

    @Query("SELECT * FROM currency_pairs WHERE terminalId = :terminalId")
    fun getForexPairs(terminalId: Int): kotlinx.coroutines.flow.Flow<List<CurrencyPair>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForexPairs(pairs: List<CurrencyPair>)

    @Query("DELETE FROM currency_pairs WHERE terminalId = :terminalId")
    suspend fun deleteAllForexPairs(terminalId: Int)

    @Transaction
    suspend fun updateForexPairs(terminalId: Int, pairs: List<CurrencyPair>) {
        deleteAllForexPairs(terminalId)
        insertForexPairs(pairs)
    }
}
