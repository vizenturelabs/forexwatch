package dev.shorthouse.coinwatch.data.source.local.datastore.favourites

import dev.shorthouse.coinwatch.data.source.local.datastore.common.PairSort
import kotlinx.serialization.Serializable

@Serializable
data class FavouritesPreferences(
    val isFavouritesCondensed: Boolean = false,
    val coinSort: PairSort = PairSort.MarketCap,
)
