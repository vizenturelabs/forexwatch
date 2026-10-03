package com.vizenture.forexwatch.domain.usecase.favourites

import com.vizenture.forexwatch.data.repository.favouriteSymbol.FavouriteForexSymbolRepository
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import javax.inject.Inject

class ToggleIsForexPairFavouriteUseCase @Inject constructor(
    private val repository: FavouriteForexSymbolRepository
) {
    suspend operator fun invoke(favouriteSymbol: FavouriteSymbol): Boolean {
        return repository.toggleIsForexPairFavourite(favouriteSymbol)
    }
}
