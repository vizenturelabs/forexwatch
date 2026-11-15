package com.vizenture.forexwatch.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.domain.manager.TerminalManager
import com.vizenture.forexwatch.domain.usecase.market.GetForexPairsUseCase
import com.vizenture.forexwatch.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getForexPairsUseCase: GetForexPairsUseCase,
    private val terminalManager: TerminalManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle(""))
    val uiState = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.value = when (val currentState = _uiState.value) {
            is SearchUiState.Idle -> currentState.copy(queryState = query)
            is SearchUiState.Success -> currentState.copy(results = persistentListOf(), queryState = query)
            is SearchUiState.Error -> currentState.copy(message = null, queryState = query)
            is SearchUiState.Loading -> currentState.copy(queryState = query)
        }

        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle(query)
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading(query)

            val terminalId = terminalManager.getActiveTerminalId().toInt()

            // FIX: Query LOCAL cached pairs instead of global Twelve Data API.
            // This prevents StringIndexOutOfBoundsException in DetailsViewModel
            // which expects exactly 6-character Forex symbols (e.g., "EURUSD").
            // Global search returns stocks like "AAPL" (4 chars) which crash the app.
            getForexPairsUseCase(
                terminalId = terminalId,
                category = MarketCategory.Major, // Ignored by UseCase for cached pairs
                currency = Currency.USD           // Ignored by UseCase for cached pairs
            ).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        val filteredResults = result.data
                            .filter { pair ->
                                pair.symbol.contains(query, ignoreCase = true) ||
                                    pair.name.contains(query, ignoreCase = true)
                            }
                            .map { pair ->
                                SearchResult(
                                    symbol = pair.symbol,
                                    name = pair.name,
                                    type = "Forex"
                                )
                            }
                            .toImmutableList()

                        _uiState.value = SearchUiState.Success(
                            results = filteredResults,
                            queryState = query
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = SearchUiState.Error(
                            message = result.message,
                            queryState = query
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = SearchUiState.Loading(query)
                    }
                }
            }
        }
    }
}
