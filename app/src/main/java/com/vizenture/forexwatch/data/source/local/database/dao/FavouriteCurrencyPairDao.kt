package com.vizenture.forexwatch.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteCurrencyPairDao {

    @Query("SELECT * FROM favourite_currency_pairs WHERE terminalId = :terminalId")
    fun getFavouriteForexPairs(terminalId: Int): Flow<List<FavouriteCurrencyPair>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteForexPairs(favouritePairs: List<FavouriteCurrencyPair>)

    @Query("DELETE FROM favourite_currency_pairs WHERE terminalId = :terminalId")
    fun deleteAllFavouriteForexPairs(terminalId: Int)

    @Transaction
    fun updateFavouriteForexPairs(terminalId: Int, favouritePairs: List<FavouriteCurrencyPair>) {
        deleteAllFavouriteForexPairs(terminalId)
        insertFavouriteForexPairs(favouritePairs)
    }
}
