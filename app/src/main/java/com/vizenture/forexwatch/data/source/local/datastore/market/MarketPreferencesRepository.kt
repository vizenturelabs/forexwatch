package com.vizenture.forexwatch.data.source.local.datastore.market

import androidx.datastore.core.DataStore
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class MarketPreferencesRepository @Inject constructor(
    private val marketPreferencesDataStore: DataStore<MarketPreferences>,
) {
    val marketPreferencesFlow: Flow<MarketPreferences> = marketPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading market preferences")
                emit(MarketPreferences())
            } else {
                throw exception
            }
        }

    suspend fun updateCategory(category: MarketCategory) {
        if (category != marketPreferencesFlow.first().category) {
            marketPreferencesDataStore.updateData { currentPreferences ->
                currentPreferences.copy(category = category)
            }
        }
    }
}
