package dev.shorthouse.coinwatch.domain.favourites

import dev.shorthouse.coinwatch.data.repository.favouriteCoinId.FavouriteForexPairIdRepository
import dev.shorthouse.coinwatch.data.source.local.database.model.FavouriteCoinId
import javax.inject.Inject

class ToggleIsCoinFavouriteUseCase @Inject constructor(
    private val favouriteForexPairIdRepository: FavouriteForexPairIdRepository
) {
    suspend operator fun invoke(favouriteCoinId: FavouriteCoinId): Boolean {
        return toggleIsCoinFavourite(favouriteCoinId = favouriteCoinId)
    }

    private suspend fun toggleIsCoinFavourite(favouriteCoinId: FavouriteCoinId): Boolean {
        return favouriteForexPairIdRepository.toggleIsCoinFavourite(favouriteCoinId = favouriteCoinId)
    }
}
