package com.vizenture.forexwatch.data.source.local.datastore.global

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataStore: DataStore<UserPreferences>,
) {

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading user preferences")
                emit(UserPreferences())
            } else {
                throw exception
            }
        }

    suspend fun updateStartScreen(startScreen: StartScreen) {
        if (startScreen != userPreferencesFlow.first().startScreen) {
            userPreferencesDataStore.updateData { currentPreferences ->
                currentPreferences.copy(startScreen = startScreen)
            }
        }
    }

    suspend fun updateIsDarkMode(isDarkMode: Boolean) {
        if (isDarkMode != userPreferencesFlow.first().isDarkMode) {
            userPreferencesDataStore.updateData { currentPreferences ->
                currentPreferences.copy(isDarkMode = isDarkMode)
            }
        }
    }

    suspend fun updateIsDynamicColorEnabled(isDynamicColorEnabled: Boolean) {
        if (isDynamicColorEnabled != userPreferencesFlow.first().isDynamicColorEnabled) {
            userPreferencesDataStore.updateData { currentPreferences ->
                currentPreferences.copy(isDynamicColorEnabled = isDynamicColorEnabled)
            }
        }
    }
}
