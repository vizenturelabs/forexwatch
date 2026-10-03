package com.vizenture.forexwatch.data.repository.favouriteSymbol

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import kotlinx.coroutines.flow.Flow

interface FavouriteForexSymbolRepository {
    fun isForexPairFavourite(symbol: String, terminalId: Int): Flow<Result<Boolean>>
    suspend fun toggleIsForexPairFavourite(favouriteSymbol: FavouriteSymbol): Boolean
}
