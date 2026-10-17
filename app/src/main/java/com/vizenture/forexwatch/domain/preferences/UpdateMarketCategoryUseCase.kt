package com.vizenture.forexwatch.domain.preferences

import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import com.vizenture.forexwatch.data.source.local.datastore.market.MarketPreferencesRepository
import javax.inject.Inject

class UpdateMarketCategoryUseCase @Inject constructor(
    private val marketPreferencesRepository: MarketPreferencesRepository,
) {
    suspend operator fun invoke(category: MarketCategory) {
        updateMarketCategory(category = category)
    }

    private suspend fun updateMarketCategory(category: MarketCategory) {
        marketPreferencesRepository.updateCategory(category = category)
    }
}
