package com.vizenture.forexwatch.data.mapper

import com.vizenture.forexwatch.model.MarketStats
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.model.Price
import javax.inject.Inject

class MarketStatsMapper @Inject constructor() {
    fun mapToDomain(apiModel: Any?): MarketStats? {
        // Stub: Alpha Vantage does not provide global market stats for Forex.
        // This mapper is kept for structural compatibility but returns null.
        return null
    }
}
