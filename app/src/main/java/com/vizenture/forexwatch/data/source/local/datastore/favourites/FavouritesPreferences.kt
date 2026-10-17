package com.vizenture.forexwatch.data.source.local.datastore.favourites

import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import kotlinx.serialization.Serializable

@Serializable
data class FavouritesPreferences(
    val isFavouritesCondensed: Boolean = false,
    val category: MarketCategory = MarketCategory.Major,
)
