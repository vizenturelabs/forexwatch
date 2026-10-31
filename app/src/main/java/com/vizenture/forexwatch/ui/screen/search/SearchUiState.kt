package com.vizenture.forexwatch.ui.screen.search

import com.vizenture.forexwatch.model.SearchResult
import kotlinx.collections.immutable.ImmutableList

sealed interface SearchUiState {
    data class Idle(val queryState: String) : SearchUiState
    data class Success(val results: ImmutableList<SearchResult>, val queryState: String) : SearchUiState
    data class Error(val message: String?, val queryState: String) : SearchUiState
    data object Loading : SearchUiState
}
