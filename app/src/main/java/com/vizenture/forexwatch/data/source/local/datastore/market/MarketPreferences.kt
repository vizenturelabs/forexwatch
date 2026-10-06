package com.vizenture.forexwatch.data.source.local.datastore.market

import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import kotlinx.serialization.Serializable

@Serializable
data class MarketPreferences(
    val coinSort: PairSort = PairSort.Volume,
)
