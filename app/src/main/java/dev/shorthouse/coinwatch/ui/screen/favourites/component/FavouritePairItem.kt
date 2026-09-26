package com.vizenture.forexwatch.ui.screen.favourites.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.ui.component.PercentageChange
import com.vizenture.forexwatch.ui.component.StaticPriceGraph
import com.vizenture.forexwatch.ui.preview.FavouriteCurrencyPairPreviewProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.vizenture.forexwatch.ui.preview.AppPreviewWrapper

@Composable
fun FavouritePairItem(
    favouritePair: FavouriteCurrencyPair,
    onPairClick: (FavouriteCurrencyPair) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.clickable { onPairClick(favouritePair) }
    ) {
        Column {
            Column(modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = favouritePair.symbol.take(3),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = favouritePair.symbol,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = favouritePair.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = favouritePair.currentPrice.formattedAmount,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row {
                    Text(
                        text = "Bid: ${favouritePair.bid.formattedAmount}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Ask: ${favouritePair.ask.formattedAmount}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Spread: ${favouritePair.spread}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                PercentageChange(percentage = favouritePair.priceChangePercentage24h)
            }

            Spacer(Modifier.height(12.dp))

            if (favouritePair.priceHistory24h.isNotEmpty()) {
                StaticPriceGraph(
                    prices = favouritePair.priceHistory24h,
                    priceChangePercentage = favouritePair.priceChangePercentage24h,
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth()
                        .testTag("priceGraph ${favouritePair.symbol}")
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.empty_chart_message_short),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
@Preview
@PreviewWrapper(wrapper = AppPreviewWrapper::class)
private fun FavouritePairItemPreview(
    @PreviewParameter(FavouriteCurrencyPairPreviewProvider::class) favouritePair: FavouriteCurrencyPair,
) {
    FavouritePairItem(
        favouritePair = favouritePair,
        onPairClick = {}
    )
}
