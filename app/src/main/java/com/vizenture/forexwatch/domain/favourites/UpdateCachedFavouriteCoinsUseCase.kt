package dev.shorthouse.coinwatch.domain.favourites

import dev.shorthouse.coinwatch.common.Result
import dev.shorthouse.coinwatch.data.repository.favouriteCoin.FavouriteForexPairRepository
import dev.shorthouse.coinwatch.data.source.local.database.model.FavouriteCoin
import dev.shorthouse.coinwatch.data.source.local.database.model.FavouriteCoinId
import dev.shorthouse.coinwatch.data.source.local.datastore.common.PairSort
import dev.shorthouse.coinwatch.data.source.local.datastore.global.Currency
import javax.inject.Inject

class UpdateCachedFavouriteCoinsUseCase @Inject constructor(
    private val favouriteForexPairRepository: FavouriteForexPairRepository,
) {
    suspend operator fun invoke(
        coinIds: List<FavouriteCoinId>,
        coinSort: PairSort,
        currency: Currency,
    ): Result<List<FavouriteCoin>> {
        return updateCachedFavouriteCoins(
            coinIds = coinIds,
            coinSort = coinSort,
            currency = currency,
        )
    }

    private suspend fun updateCachedFavouriteCoins(
        coinIds: List<FavouriteCoinId>,
        currency: Currency,
        coinSort: PairSort,
    ): Result<List<FavouriteCoin>> {
        val remoteFavouriteCoinsResult = favouriteForexPairRepository.getRemoteFavouriteCoins(
            coinIds = coinIds.map { it.id },
            coinSort = coinSort,
            currency = currency,
        )

        if (remoteFavouriteCoinsResult is Result.Success) {
            val favouriteCoins = remoteFavouriteCoinsResult.data
            favouriteForexPairRepository.updateCachedFavouriteCoins(favouriteCoins = favouriteCoins)
        }

        return remoteFavouriteCoinsResult
    }
}
