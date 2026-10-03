package dev.shorthouse.coinwatch.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.shorthouse.coinwatch.model.SearchResult
import dev.shorthouse.coinwatch.ui.screen.search.SearchUiState
import kotlinx.collections.immutable.persistentListOf

data class SearchScreenPreviewState(
    val uiState: SearchUiState,
    val searchQuery: String,
)

class SearchScreenPreviewStateProvider : PreviewParameterProvider<SearchScreenPreviewState> {
    override val values = sequenceOf(
        SearchScreenPreviewState(
            uiState = SearchUiState(
                searchResults = persistentListOf(
                    SearchResult(
                        id = "bitcoin",
                        symbol = "BTC",
                        name = "Bitcoin",
                        imageUrl = "https://cdn.coinranking.com/bOabBYkcX/bitcoin_btc.svg"
                    ),
                    SearchResult(
                        id = "ethereum",
                        symbol = "ETH",
                        name = "Ethereum",
                        imageUrl = "https://cdn.coinranking.com/rk4RKHOuW/eth.svg"
                    ),
                    SearchResult(
                        id = "tether",
                        symbol = "USDT",
                        name = "Tether",
                        imageUrl = "https://cdn.coinranking.com/mgHqwlCLj/usdt.svg"
                    )
                ),
                queryHasNoResults = false
            ),
            searchQuery = "bit"
        ),
        SearchScreenPreviewState(
            uiState = SearchUiState(
                searchResults = persistentListOf(),
                queryHasNoResults = true
            ),
            searchQuery = "no results"
        ),
        SearchScreenPreviewState(
            uiState = SearchUiState(
                errorMessage = "Error searching coins"
            ),
            searchQuery = "error"
        ),
        SearchScreenPreviewState(
            uiState = SearchUiState(
                isSearching = true
            ),
            searchQuery = "loading"
        ),
        SearchScreenPreviewState(
            uiState = SearchUiState(),
            searchQuery = ""
        )
    )
}
