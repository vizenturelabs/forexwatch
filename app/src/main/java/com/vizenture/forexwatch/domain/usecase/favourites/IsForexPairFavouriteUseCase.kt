package com.vizenture.forexwatch.domain.usecase.favourites

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.repository.favouriteSymbol.FavouriteForexSymbolRepository
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsForexPairFavouriteUseCase @Inject constructor(
    private val repository: FavouriteForexSymbolRepository
) {
    operator fun invoke(favouriteSymbol: FavouriteSymbol): Flow<Result<Boolean>> {
        return repository.isForexPairFavourite(favouriteSymbol.symbol, favouriteSymbol.terminalId)
    }
}
