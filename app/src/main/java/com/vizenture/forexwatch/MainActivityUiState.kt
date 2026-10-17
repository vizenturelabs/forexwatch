package com.vizenture.forexwatch

import com.vizenture.forexwatch.navigation.NavigationBarScreen

data class MainActivityUiState(
    val startScreen: NavigationBarScreen = NavigationBarScreen.Market,
    val isDarkMode: Boolean = false,
    val isDynamicColorEnabled: Boolean = false,
    val isLoading: Boolean = true
)
