package dev.shorthouse.coinwatch.domain.favourites

import dev.shorthouse.coinwatch.common.Result
import dev.shorthouse.coinwatch.data.repository.favouriteCoin.FavouriteForexPairRepository
import dev.shorthouse.coinwatch.data.source.local.database.model.FavouriteCoin
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteCoinsUseCase @Inject constructor(
    private val favouriteForexPairRepository: FavouriteForexPairRepository
) {
    operator fun invoke(): Flow<Result<List<FavouriteCoin>>> {
        return getFavouriteCoins()
    }

    private fun getFavouriteCoins(): Flow<Result<List<FavouriteCoin>>> {
        return favouriteForexPairRepository.getCachedFavouriteCoins()
    }
}
