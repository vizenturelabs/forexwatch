package com.vizenture.forexwatch.ui.screen.market.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.ui.component.PercentageChange
import com.vizenture.forexwatch.ui.preview.CurrencyPairPreviewProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.vizenture.forexwatch.ui.preview.AppPreviewWrapper

@Composable
fun MarketPairItem(
    pair: CurrencyPair,
    onPairClick: (CurrencyPair) -> Unit,
    cardShape: Shape,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = cardShape,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPairClick(pair) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Replaced AsyncImage with a colored box showing the first 3 letters of the symbol
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = pair.symbol.take(3),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pair.symbol,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = pair.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(4.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    pair.currentPrice.formattedAmount,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Spread: ${pair.spread}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                PercentageChange(percentage = pair.priceChangePercentage24h)
            }
        }
    }
}

@Composable
@Preview
@PreviewWrapper(wrapper = AppPreviewWrapper::class)
private fun MarketPairItemPreview(
    @PreviewParameter(CurrencyPairPreviewProvider::class) pair: CurrencyPair,
) {
    MarketPairItem(
        pair = pair,
        onPairClick = {},
        cardShape = MaterialTheme.shapes.medium
    )
}
