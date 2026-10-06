package com.vizenture.forexwatch.ui.screen.favourites.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair

@Composable
fun FavouritePairItem(
    favouritePair: FavouriteCurrencyPair,
    onNavigateDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNavigateDetails(favouritePair.symbol) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favouritePair.symbol,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = favouritePair.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = favouritePair.currentPrice.formattedAmount,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = favouritePair.priceChangePercentage24h.formattedAmount,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (favouritePair.priceChangePercentage24h.isPositive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}
