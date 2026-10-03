package dev.shorthouse.coinwatch.domain.favourites

import dev.shorthouse.coinwatch.common.Result
import dev.shorthouse.coinwatch.data.repository.favouriteCoinId.FavouriteForexPairIdRepository
import dev.shorthouse.coinwatch.data.source.local.database.model.FavouriteCoinId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteCoinIdsUseCase @Inject constructor(
    private val favouriteForexPairIdRepository: FavouriteForexPairIdRepository
) {
    operator fun invoke(): Flow<Result<List<FavouriteCoinId>>> {
        return getFavouriteCoinIds()
    }

    private fun getFavouriteCoinIds(): Flow<Result<List<FavouriteCoinId>>> {
        return favouriteForexPairIdRepository.getFavouriteCoinIds()
    }
}
