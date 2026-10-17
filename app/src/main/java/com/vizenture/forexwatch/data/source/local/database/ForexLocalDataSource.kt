package com.vizenture.forexwatch.data.source.local.database

import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import kotlinx.coroutines.flow.Flow

interface ForexLocalDataSource {
    fun getForexPairs(terminalId: Int): Flow<List<CurrencyPair>>
    suspend fun updateForexPairs(terminalId: Int, pairs: List<CurrencyPair>)

    fun getFavouriteForexPairs(terminalId: Int): Flow<List<FavouriteCurrencyPair>>
    suspend fun updateFavouriteForexPairs(terminalId: Int, pairs: List<FavouriteCurrencyPair>)

    // NEW: Expose the bookmarked symbols
    fun getFavouriteSymbols(terminalId: Int): Flow<List<FavouriteSymbol>>

    fun isForexPairFavourite(symbol: String, terminalId: Int): Flow<Boolean>
    suspend fun isForexPairFavouriteOneShot(symbol: String, terminalId: Int): Boolean
    suspend fun insertFavouriteSymbol(favouriteSymbol: FavouriteSymbol)
    suspend fun deleteFavouriteSymbol(favouriteSymbol: FavouriteSymbol)
}
