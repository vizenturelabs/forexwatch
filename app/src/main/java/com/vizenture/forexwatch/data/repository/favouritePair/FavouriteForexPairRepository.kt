package com.vizenture.forexwatch.data.repository.favouritePair

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import kotlinx.coroutines.flow.Flow

interface FavouriteForexPairRepository {
    suspend fun getRemoteFavouriteForexPairs(
        terminalId: Int,
        pairSort: PairSort,
        currency: Currency,
    ): Result<List<FavouriteCurrencyPair>>

    fun getCachedFavouriteForexPairs(terminalId: Int): Flow<Result<List<FavouriteCurrencyPair>>>
    suspend fun updateCachedFavouriteForexPairs(terminalId: Int, favouritePairs: List<FavouriteCurrencyPair>)
}
