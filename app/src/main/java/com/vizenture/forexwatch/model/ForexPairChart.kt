package com.vizenture.forexwatch.model

import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import kotlinx.collections.immutable.ImmutableList

data class ForexPairChart(
    val currency: Currency,
    val priceHistory: ImmutableList<OhlcEntry>,
    val periodPriceChangePercentage: Percentage
)
