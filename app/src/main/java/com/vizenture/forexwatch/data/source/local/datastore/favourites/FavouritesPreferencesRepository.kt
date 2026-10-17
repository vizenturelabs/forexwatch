package com.vizenture.forexwatch.data.source.local.datastore.favourites

import androidx.datastore.core.DataStore
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class FavouritesPreferencesRepository @Inject constructor(
    private val favouritesPreferencesDataStore: DataStore<FavouritesPreferences>,
) {
    val favouritesPreferencesFlow: Flow<FavouritesPreferences> = favouritesPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading favourites preferences")
                emit(FavouritesPreferences())
            } else {
                throw exception
            }
        }

    suspend fun updateIsFavouritesCondensed(isCondensed: Boolean) {
        if (isCondensed != favouritesPreferencesFlow.first().isFavouritesCondensed) {
            favouritesPreferencesDataStore.updateData { currentPreferences ->
                currentPreferences.copy(isFavouritesCondensed = isCondensed)
            }
        }
    }

    suspend fun updateCategory(category: MarketCategory) {
        if (category != favouritesPreferencesFlow.first().category) {
            favouritesPreferencesDataStore.updateData { currentPreferences ->
                currentPreferences.copy(category = category)
            }
        }
    }
}
