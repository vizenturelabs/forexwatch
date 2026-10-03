package com.vizenture.forexwatch.ui.screen.favourites

import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair

sealed interface FavouritesUiState {
    data class Success(val favourites: List<FavouriteCurrencyPair>) : FavouritesUiState
    data class Error(val message: String?) : FavouritesUiState
    data object Loading : FavouritesUiState
}
