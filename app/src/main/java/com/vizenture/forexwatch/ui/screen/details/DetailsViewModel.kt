package com.vizenture.forexwatch.ui.screen.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.common.Constants.PARAM_PAIR_SYMBOL
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.local.datastore.global.UserPreferencesRepository
import com.vizenture.forexwatch.domain.manager.TerminalManager
import com.vizenture.forexwatch.domain.usecase.details.GetForexPairChartUseCase
import com.vizenture.forexwatch.domain.usecase.details.GetForexPairDetailsUseCase
import com.vizenture.forexwatch.domain.usecase.favourites.IsForexPairFavouriteUseCase
import com.vizenture.forexwatch.domain.usecase.favourites.ToggleIsForexPairFavouriteUseCase
import com.vizenture.forexwatch.ui.model.ChartPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getForexPairDetailsUseCase: GetForexPairDetailsUseCase,
    private val getForexPairChartUseCase: GetForexPairChartUseCase,
    private val isForexPairFavouriteUseCase: IsForexPairFavouriteUseCase,
    private val toggleIsForexPairFavouriteUseCase: ToggleIsForexPairFavouriteUseCase,
    private val terminalManager: TerminalManager,
    private val userPreferencesRepository: UserPreferencesRepository, // FIX: Inject Repository, not the data class
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val pairSymbol = savedStateHandle.get<String>(PARAM_PAIR_SYMBOL)
    private val chartPeriodFlow = MutableStateFlow(ChartPeriod.Day)

    // Hardcoded for Phase 4 Proof of Concept
    private val currentCurrency = Currency.USD

    init {
        initialiseUiState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initialiseUiState() {
        if (pairSymbol == null) {
            _uiState.update { DetailsUiState.Error("Invalid pair symbol") }
            return
        }

        val terminalId = terminalManager.getActiveTerminalId().toInt()

        val pairDetailsFlow = getForexPairDetailsUseCase(
            symbol = pairSymbol,
            currency = currentCurrency
        )

        // FIX: Map the repository flow to extract the refreshInterval, then combine with chartPeriodFlow
        val refreshIntervalFlow = userPreferencesRepository.userPreferencesFlow.map { it.refreshInterval }

        val pairChartFlow = combine(chartPeriodFlow, refreshIntervalFlow) { chartPeriod, refreshInterval ->
            getForexPairChartUseCase(
                symbol = pairSymbol,
                chartPeriod = chartPeriod,
                currency = currentCurrency,
                refreshIntervalMillis = refreshInterval.millis
            )
        }.flatMapLatest { it }

        val isPairFavouriteFlow = isForexPairFavouriteUseCase(
            favouriteSymbol = FavouriteSymbol(symbol = pairSymbol, terminalId = terminalId)
        )

        combine(
            pairDetailsFlow,
            pairChartFlow,
            isPairFavouriteFlow
        ) { pairDetailsResult, pairChartResult, isPairFavouriteResult ->
            when {
                pairDetailsResult is Result.Error -> _uiState.update { DetailsUiState.Error(pairDetailsResult.message) }
                pairChartResult is Result.Error -> _uiState.update { DetailsUiState.Error(pairChartResult.message) }
                isPairFavouriteResult is Result.Error -> _uiState.update { DetailsUiState.Error(isPairFavouriteResult.message) }
                pairDetailsResult is Result.Success &&
                    pairChartResult is Result.Success &&
                    isPairFavouriteResult is Result.Success -> {
                    _uiState.update {
                        DetailsUiState.Success(
                            pairDetails = pairDetailsResult.data,
                            pairChart = pairChartResult.data,
                            chartPeriod = chartPeriodFlow.value,
                            isPairFavourite = isPairFavouriteResult.data
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateChartPeriod(chartPeriod: ChartPeriod) {
        chartPeriodFlow.value = chartPeriod
    }

    fun toggleIsPairFavourite() {
        if (pairSymbol == null) return
        viewModelScope.launch {
            val terminalId = terminalManager.getActiveTerminalId().toInt()
            val favouriteSymbol = FavouriteSymbol(symbol = pairSymbol, terminalId = terminalId)
            toggleIsForexPairFavouriteUseCase(favouriteSymbol)
        }
    }
}
