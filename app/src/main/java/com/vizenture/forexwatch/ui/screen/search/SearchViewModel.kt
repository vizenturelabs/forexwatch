package com.vizenture.forexwatch.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.domain.usecase.search.GetForexSearchResultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getForexSearchResultsUseCase: GetForexSearchResultsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle(""))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.value = when (val currentState = _uiState.value) {
            is SearchUiState.Idle -> currentState.copy(queryState = query)
            is SearchUiState.Success -> currentState.copy(results = persistentListOf(), queryState = query)
            is SearchUiState.Error -> currentState.copy(message = null, queryState = query)
            is SearchUiState.Loading -> SearchUiState.Idle(query)
        }

        if (query.isBlank()) {
            searchJob?.cancel()
            _uiState.value = SearchUiState.Idle(query)
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500.milliseconds) // Updated to use Duration API
            _uiState.value = SearchUiState.Loading
            getForexSearchResultsUseCase(query).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = SearchUiState.Success(
                            results = result.data.toImmutableList(),
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
                        _uiState.value = SearchUiState.Loading
                    }
                }
            }
        }
    }
}
