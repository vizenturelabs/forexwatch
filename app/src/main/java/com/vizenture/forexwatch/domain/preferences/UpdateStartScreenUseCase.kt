package com.vizenture.forexwatch.domain.preferences

import com.vizenture.forexwatch.data.source.local.datastore.global.StartScreen
import com.vizenture.forexwatch.data.source.local.datastore.global.UserPreferencesRepository
import javax.inject.Inject

class UpdateStartScreenUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(startScreen: StartScreen) {
        updateStartScreen(startScreen = startScreen)
    }

    private suspend fun updateStartScreen(startScreen: StartScreen) {
        userPreferencesRepository.updateStartScreen(startScreen = startScreen)
    }
}
