package com.vizenture.forexwatch.ui.screen.market

import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.ui.model.TimeOfDay
import kotlinx.collections.immutable.ImmutableList

data class MarketUiState(
    val pairs: ImmutableList<CurrencyPair>,
    val category: MarketCategory,
    val timeOfDay: TimeOfDay,
    val marketChangePercentage24h: Percentage?,
    val currency: Currency,
    val isRefreshing: Boolean = false,
    val errorMessageIds: List<Int> = emptyList(),
    val isLoading: Boolean = false
)
