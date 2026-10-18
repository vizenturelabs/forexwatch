package com.vizenture.forexwatch.data.source.local.datastore.global

import androidx.annotation.StringRes
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.data.source.local.datastore.common.RefreshInterval
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val startScreen: StartScreen = StartScreen.Market,
    val refreshInterval: RefreshInterval = RefreshInterval.MIN_60,
    val isDarkMode: Boolean = false,
    val isDynamicColorEnabled: Boolean = false,
)

enum class Currency {
    USD,
    GBP,
    EUR
}

// FIX: Added @param: to explicitly target the constructor parameter
enum class StartScreen(@param:StringRes val nameId: Int) {
    Market(R.string.market_screen),
    Favourites(R.string.favourites_screen),
    Search(R.string.search_screen)
}
