package com.vizenture.forexwatch.ui.screen.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.repository.favouritePair.FavouriteForexPairRepository
import com.vizenture.forexwatch.domain.manager.TerminalManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favouriteForexPairRepository: FavouriteForexPairRepository,
    private val terminalManager: TerminalManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavouritesUiState>(FavouritesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        val terminalId = terminalManager.getActiveTerminalId().toInt()

        favouriteForexPairRepository.getFavouriteForexPairs(terminalId)
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { FavouritesUiState.Success(favourites = result.data) }
                    }
                    is Result.Error -> {
                        _uiState.update { FavouritesUiState.Error(result.message) }
                    }
                    is Result.Loading -> {
                        _uiState.update { FavouritesUiState.Loading }
                    }
                }
            }.launchIn(viewModelScope)
    }
}
