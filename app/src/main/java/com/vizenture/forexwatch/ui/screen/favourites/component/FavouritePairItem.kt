package com.vizenture.forexwatch.ui.screen.favourites.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.ui.component.PercentageChange
import com.vizenture.forexwatch.ui.component.StaticPriceGraph

@Composable
fun FavouritePairItem(
    favouritePair: FavouriteCurrencyPair,
    onNavigateDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.clickable { onNavigateDetails(favouritePair.symbol) }
    ) {
        Column {
            Column(modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = favouritePair.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                        Text(
                            text = favouritePair.symbol,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = favouritePair.currentPrice.formattedAmount,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                PercentageChange(percentage = favouritePair.priceChangePercentage24h)
            }
            Spacer(Modifier.height(12.dp))
            if (favouritePair.priceHistory24h.isNotEmpty()) {
                StaticPriceGraph(
                    prices = favouritePair.priceHistory24h,
                    priceChangePercentage = favouritePair.priceChangePercentage24h,
                    modifier = Modifier.height(70.dp).fillMaxWidth()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(70.dp).fillMaxWidth()
                ) {
                    Text(
                        text = "No chart data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
