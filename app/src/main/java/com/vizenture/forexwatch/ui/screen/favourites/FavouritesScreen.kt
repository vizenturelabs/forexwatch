package com.vizenture.forexwatch.ui.screen.favourites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FavouritesScreen(
    onNavigateDetails: (String) -> Unit,
    viewModel: FavouritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is FavouritesUiState.Loading -> {
            Text(text = "Loading favourites...", modifier = Modifier.padding(16.dp))
        }
        is FavouritesUiState.Error -> {
            Text(text = "Error loading favourites", modifier = Modifier.padding(16.dp))
        }
        is FavouritesUiState.Success -> {
            val pairs = (uiState as FavouritesUiState.Success).favourites
            if (pairs.isEmpty()) {
                Text(text = "No favourite pairs yet.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(pairs, key = { it.symbol }) { pair ->
                        // Simplified list item to ensure compilation.
                        // You can swap this with a custom FavouritePairItem composable later.
                        Text(
                            text = "${pair.symbol} - ${pair.currentPrice.formattedAmount}",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onNavigateDetails(pair.symbol) }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

