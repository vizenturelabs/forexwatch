package com.vizenture.forexwatch.domain.preferences

import com.vizenture.forexwatch.data.source.local.datastore.market.MarketPreferences
import com.vizenture.forexwatch.data.source.local.datastore.market.MarketPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMarketPreferencesUseCase @Inject constructor(
    private val marketPreferencesRepository: MarketPreferencesRepository,
) {
    operator fun invoke(): Flow<MarketPreferences> {
        return getMarketPreferences()
    }

    private fun getMarketPreferences(): Flow<MarketPreferences> {
        return marketPreferencesRepository.marketPreferencesFlow
    }
}
