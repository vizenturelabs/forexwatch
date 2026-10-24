package com.vizenture.forexwatch.ui.screen.favourites.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.ui.component.PercentageChange

@Composable
fun FavouriteCondensedItem(
    favouritePair: FavouriteCurrencyPair,
    onNavigateDetails: (String) -> Unit,
    cardShape: Shape,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = cardShape,
        modifier = modifier.fillMaxWidth().clickable { onNavigateDetails(favouritePair.symbol) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
            Spacer(Modifier.width(4.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = favouritePair.currentPrice.formattedAmount,
                    style = MaterialTheme.typography.bodyMedium
                )
                PercentageChange(percentage = favouritePair.priceChangePercentage24h)
            }
        }
    }
}
