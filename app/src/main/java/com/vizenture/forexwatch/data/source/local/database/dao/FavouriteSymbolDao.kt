package com.vizenture.forexwatch.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteSymbolDao {

    @Query("SELECT * FROM favourite_symbols WHERE terminalId = :terminalId")
    fun getFavouriteSymbols(terminalId: Int): Flow<List<FavouriteSymbol>>

    @Query("SELECT COUNT(1) FROM favourite_symbols WHERE symbol = :symbol AND terminalId = :terminalId")
    fun isForexPairFavourite(symbol: String, terminalId: Int): Flow<Boolean>

    @Query("SELECT COUNT(1) FROM favourite_symbols WHERE symbol = :symbol AND terminalId = :terminalId")
    suspend fun isForexPairFavouriteOneShot(symbol: String, terminalId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favouriteSymbol: FavouriteSymbol)

    @Delete
    suspend fun delete(favouriteSymbol: FavouriteSymbol)
}
