package com.vizenture.forexwatch.ui.screen.favourites

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.repository.favouritePair.FavouriteForexPairRepository
import com.vizenture.forexwatch.domain.manager.TerminalManager
import com.vizenture.forexwatch.domain.preferences.GetFavouritesPreferencesUseCase
import com.vizenture.forexwatch.domain.preferences.UpdateIsFavouritesCondensedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favouriteForexPairRepository: FavouriteForexPairRepository,
    private val terminalManager: TerminalManager,
    private val getFavouritesPreferencesUseCase: GetFavouritesPreferencesUseCase,
    private val updateIsFavouritesCondensedUseCase: UpdateIsFavouritesCondensedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        _uiState.update { it.copy(isLoading = true) }
        val terminalId = terminalManager.getActiveTerminalId().toInt()

        combine(
            favouriteForexPairRepository.getFavouriteForexPairs(terminalId),
            getFavouritesPreferencesUseCase()
        ) { pairsResult, preferences ->
            when (pairsResult) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            favouritePairs = pairsResult.data.toImmutableList(),
                            isFavouritePairsEmpty = pairsResult.data.isEmpty(),
                            isFavouritesCondensed = preferences.isFavouritesCondensed
                        )
                    }
                }
                is Result.Error -> {
                    addErrorMessage(R.string.error_local_favourite_coins)
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun pullRefreshFavouritePairs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            delay(250.milliseconds)
            // Repository flow will re-emit if underlying data changes.
            // Triggering a manual refresh can be added here if needed.
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun dismissErrorMessage(@StringRes dismissedErrorMessageId: Int) {
        _uiState.update {
            val errorMessageIds = it.errorMessageIds.filterNot { id -> id == dismissedErrorMessageId }
            it.copy(errorMessageIds = errorMessageIds)
        }
    }

    fun updateIsFavouritesCondensed(isCondensed: Boolean) {
        viewModelScope.launch {
            updateIsFavouritesCondensedUseCase(isCondensed = isCondensed)
        }
    }

    private fun addErrorMessage(@StringRes errorMessageId: Int) {
        _uiState.update {
            it.copy(errorMessageIds = it.errorMessageIds + errorMessageId)
        }
    }
}
