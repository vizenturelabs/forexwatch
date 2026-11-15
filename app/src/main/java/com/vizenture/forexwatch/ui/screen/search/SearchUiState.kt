package com.vizenture.forexwatch.ui.screen.search

import com.vizenture.forexwatch.model.SearchResult
import kotlinx.collections.immutable.ImmutableList

sealed interface SearchUiState {
    val queryState: String

    data class Idle(override val queryState: String) : SearchUiState
    data class Success(val results: ImmutableList<SearchResult>, override val queryState: String) : SearchUiState
    data class Error(val message: String?, override val queryState: String) : SearchUiState
    data class Loading(override val queryState: String) : SearchUiState
}
