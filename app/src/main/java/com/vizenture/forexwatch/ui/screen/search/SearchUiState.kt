package dev.shorthouse.coinwatch.ui.screen.search

import dev.shorthouse.coinwatch.model.SearchResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class SearchUiState(
    val searchResults: ImmutableList<SearchResult> = persistentListOf(),
    val queryHasNoResults: Boolean = false,
    val errorMessage: String? = null,
    val isSearching: Boolean = false
)
