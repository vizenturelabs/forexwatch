package com.vizenture.forexwatch.ui.screen.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.common.Constants
import com.vizenture.forexwatch.common.Constants.PARAM_PAIR_SYMBOL
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.local.datastore.global.UserPreferencesRepository
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.domain.manager.TerminalManager
import com.vizenture.forexwatch.domain.usecase.details.GetForexPairChartUseCase
import com.vizenture.forexwatch.domain.usecase.details.GetForexPairDetailsUseCase
import com.vizenture.forexwatch.domain.usecase.favourites.IsForexPairFavouriteUseCase
import com.vizenture.forexwatch.domain.usecase.favourites.ToggleIsForexPairFavouriteUseCase
import com.vizenture.forexwatch.ui.component.IndicatorPoint
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

enum class TechnicalIndicator(val displayName: String, val apiEndpoint: String, val timePeriod: Int) {
    NONE("None", "", 0),
    SMA_20("SMA 20", "sma", 20),
    EMA_50("EMA 50", "ema", 50),
    RSI_14("RSI 14", "rsi", 14)
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getForexPairDetailsUseCase: GetForexPairDetailsUseCase,
    private val getForexPairChartUseCase: GetForexPairChartUseCase,
    private val isForexPairFavouriteUseCase: IsForexPairFavouriteUseCase,
    private val toggleIsForexPairFavouriteUseCase: ToggleIsForexPairFavouriteUseCase,
    private val terminalManager: TerminalManager,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val forexNetworkDataSource: ForexNetworkDataSource,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val pairSymbol = savedStateHandle.get<String>(PARAM_PAIR_SYMBOL)
    private val chartPeriodFlow = MutableStateFlow(ChartPeriod.Day)

    private val currentCurrency = Currency.USD

    private val _selectedIndicator = MutableStateFlow(TechnicalIndicator.NONE)
    val selectedIndicator = _selectedIndicator.asStateFlow()

    private val _indicatorData = MutableStateFlow<List<IndicatorPoint>>(emptyList())
    val indicatorData = _indicatorData.asStateFlow()

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
        if (_selectedIndicator.value != TechnicalIndicator.NONE) {
            fetchIndicatorData(_selectedIndicator.value)
        }
    }

    fun updateSelectedIndicator(indicator: TechnicalIndicator) {
        _selectedIndicator.value = indicator
        if (indicator != TechnicalIndicator.NONE && pairSymbol != null) {
            fetchIndicatorData(indicator)
        } else {
            _indicatorData.value = emptyList()
        }
    }

    private fun fetchIndicatorData(indicator: TechnicalIndicator) {
        if (pairSymbol == null) return

        viewModelScope.launch {
            try {
                val fromSymbol = pairSymbol.substring(0, 3)
                val toSymbol = pairSymbol.substring(3, 6)
                val tdSymbol = Constants.getStandardForexSymbol(fromSymbol, toSymbol)

                val interval = when (chartPeriodFlow.value) {
                    ChartPeriod.Hour -> "5min"
                    ChartPeriod.Day -> "1h"
                    ChartPeriod.Week, ChartPeriod.Month, ChartPeriod.ThreeMonth -> "1day"
                    ChartPeriod.Year -> "1week"
                    ChartPeriod.FiveYear -> "1month"
                }

                val response = forexNetworkDataSource.getTechnicalIndicator(
                    symbol = tdSymbol,
                    interval = interval,
                    endpoint = indicator.apiEndpoint,
                    timePeriod = indicator.timePeriod
                )

                if (response.status == "ok" && !response.values.isNullOrEmpty()) {
                    val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val dailyTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                    val points = response.values.mapNotNull {
                        val ts = parseTimestamp(it.datetime, timeFormatter, dailyTimeFormatter)
                        // FIX: Added safe call operator (?.) because it.value is now a nullable String?
                        val y = it.value?.toFloatOrNull() ?: return@mapNotNull null
                        IndicatorPoint(ts.toFloat(), y)
                    }
                    _indicatorData.value = points
                } else {
                    _indicatorData.value = emptyList()
                }
            } catch (_: Exception) {
                _indicatorData.value = emptyList()
            }
        }
    }

    private fun parseTimestamp(datetime: String, timeFormatter: DateTimeFormatter, dailyTimeFormatter: DateTimeFormatter): Long {
        return runCatching {
            if (datetime.contains(" ")) {
                LocalDateTime.parse(datetime, timeFormatter).toInstant(ZoneOffset.UTC).epochSecond
            } else {
                LocalDate.parse(datetime, dailyTimeFormatter).atStartOfDay(ZoneOffset.UTC).toEpochSecond()
            }
        }.getOrDefault(0L)
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
