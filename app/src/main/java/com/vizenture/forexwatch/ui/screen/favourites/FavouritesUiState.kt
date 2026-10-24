package com.vizenture.forexwatch.ui.screen.favourites

import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class FavouritesUiState(
    val favouritePairs: ImmutableList<FavouriteCurrencyPair> = persistentListOf(),
    val isFavouritesCondensed: Boolean = false,
    val isFavouritePairsEmpty: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageIds: List<Int> = persistentListOf(),
)
