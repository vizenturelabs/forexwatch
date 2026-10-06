package com.vizenture.forexwatch.data.source.local.datastore.favourites

import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import kotlinx.serialization.Serializable

@Serializable
data class FavouritesPreferences(
    val isFavouritesCondensed: Boolean = false,
    val coinSort: PairSort = PairSort.Volume,
)
