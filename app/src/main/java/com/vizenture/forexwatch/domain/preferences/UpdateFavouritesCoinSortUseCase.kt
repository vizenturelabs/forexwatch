package dev.shorthouse.coinwatch.domain.preferences

import dev.shorthouse.coinwatch.data.source.local.datastore.common.PairSort
import dev.shorthouse.coinwatch.data.source.local.datastore.favourites.FavouritesPreferencesRepository
import javax.inject.Inject

class UpdateFavouritesCoinSortUseCase @Inject constructor(
    private val favouritesPreferencesRepository: FavouritesPreferencesRepository,
) {
    suspend operator fun invoke(coinSort: PairSort) {
        updateFavouritesCoinSort(coinSort = coinSort)
    }

    private suspend fun updateFavouritesCoinSort(coinSort: PairSort) {
        favouritesPreferencesRepository.updateCoinSort(coinSort = coinSort)
    }
}
