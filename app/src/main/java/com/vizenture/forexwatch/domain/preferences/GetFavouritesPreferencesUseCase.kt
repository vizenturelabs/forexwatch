package com.vizenture.forexwatch.domain.preferences

import com.vizenture.forexwatch.data.source.local.datastore.favourites.FavouritesPreferences
import com.vizenture.forexwatch.data.source.local.datastore.favourites.FavouritesPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouritesPreferencesUseCase @Inject constructor(
    private val favouritesPreferencesRepository: FavouritesPreferencesRepository,
) {
    operator fun invoke(): Flow<FavouritesPreferences> {
        return getFavouritesPreferences()
    }

    private fun getFavouritesPreferences(): Flow<FavouritesPreferences> {
        return favouritesPreferencesRepository.favouritesPreferencesFlow
    }
}
