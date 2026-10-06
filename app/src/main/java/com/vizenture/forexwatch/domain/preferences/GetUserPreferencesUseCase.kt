package com.vizenture.forexwatch.domain.preferences

import com.vizenture.forexwatch.data.source.local.datastore.global.UserPreferences
import com.vizenture.forexwatch.data.source.local.datastore.global.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<UserPreferences> {
        return getUserPreferences()
    }

    private fun getUserPreferences(): Flow<UserPreferences> {
        return userPreferencesRepository.userPreferencesFlow
    }
}
