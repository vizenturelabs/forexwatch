package com.vizenture.forexwatch.data.source.local.datastore.market

import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import kotlinx.serialization.Serializable

@Serializable
data class MarketPreferences(
    val category: MarketCategory = MarketCategory.Major,
)
