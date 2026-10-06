package com.vizenture.forexwatch

import com.vizenture.forexwatch.navigation.NavigationBarScreen

data class MainActivityUiState(
    val startScreen: NavigationBarScreen = NavigationBarScreen.Market,
    val isLoading: Boolean = false
)
