package com.vizenture.forexwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.data.source.local.datastore.global.StartScreen
import com.vizenture.forexwatch.domain.preferences.GetUserPreferencesUseCase
import com.vizenture.forexwatch.navigation.NavigationBarScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }

        getUserPreferencesUseCase()
            .onEach { userPreferences ->
                _uiState.update {
                    it.copy(
                        startScreen = when (userPreferences.startScreen) {
                            StartScreen.Market -> NavigationBarScreen.Market
                            StartScreen.Favourites -> NavigationBarScreen.Favourites
                            StartScreen.Pulse -> NavigationBarScreen.Pulse
                            StartScreen.Search -> NavigationBarScreen.Search
                        },
                        isLoading = false
                    )
                }
            }.catch {
                _uiState.update { it.copy(isLoading = false) }
            }.launchIn(viewModelScope)
    }
}
