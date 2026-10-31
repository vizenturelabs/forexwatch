package com.vizenture.forexwatch.ui.screen.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vizenture.forexwatch.ui.component.LoadingIndicator
import com.vizenture.forexwatch.ui.screen.search.component.SearchListItem
import com.vizenture.forexwatch.ui.screen.search.component.SearchQueryEmptyState
import com.vizenture.forexwatch.ui.screen.search.component.SearchResultsEmptyState

@Composable
fun SearchScreen(
    onNavigateDetails: (String) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onNavigateDetails = onNavigateDetails,
        onNavigateUp = onNavigateUp,
        onUpdateQuery = { viewModel.updateQuery(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onNavigateDetails: (String) -> Unit,
    onNavigateUp: () -> Unit,
    onUpdateQuery: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val queryState = when (uiState) {
        is SearchUiState.Idle -> uiState.queryState
        is SearchUiState.Success -> uiState.queryState
        is SearchUiState.Error -> uiState.queryState
        is SearchUiState.Loading -> ""
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = queryState,
                    onQueryChange = onUpdateQuery,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search forex pairs") },
                    leadingIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    trailingIcon = {
                        if (queryState.isNotEmpty()) {
                            IconButton(onClick = { onUpdateQuery("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    }
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {}

        when (uiState) {
            is SearchUiState.Idle -> {
                SearchQueryEmptyState(modifier = Modifier.fillMaxSize())
            }
            is SearchUiState.Loading -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }
            is SearchUiState.Success -> {
                if (uiState.results.isEmpty()) {
                    SearchResultsEmptyState(modifier = Modifier.fillMaxSize())
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.results, key = { it.symbol }) { result ->
                            SearchListItem(
                                searchResult = result,
                                onNavigateDetails = onNavigateDetails
                            )
                        }
                    }
                }
            }
            is SearchUiState.Error -> {
                SearchResultsEmptyState(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
