package com.vizenture.forexwatch.ui.screen.settings

import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.local.datastore.global.StartScreen

data class SettingsUiState(
    val currency: Currency = Currency.USD,
    val isCurrencySheetShown: Boolean = false,
    val startScreen: StartScreen = StartScreen.Market,
    val isStartScreenSheetShown: Boolean = false,
    val isDarkMode: Boolean = false,
    val isDynamicColorEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
