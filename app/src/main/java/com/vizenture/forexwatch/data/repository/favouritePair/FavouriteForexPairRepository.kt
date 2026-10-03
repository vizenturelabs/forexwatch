package com.vizenture.forexwatch.data.repository.favouritePair

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import kotlinx.coroutines.flow.Flow

interface FavouriteForexPairRepository {
    fun getFavouriteForexPairs(terminalId: Int): Flow<Result<List<FavouriteCurrencyPair>>>
}
