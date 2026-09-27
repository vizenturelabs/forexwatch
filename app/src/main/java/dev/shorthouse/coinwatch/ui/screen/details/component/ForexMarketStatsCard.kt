package com.vizenture.forexwatch.ui.screen.details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.model.ForexPairDetails
import com.vizenture.forexwatch.model.Price
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.vizenture.forexwatch.ui.preview.AppPreviewWrapper

@Composable
fun ForexMarketStatsCard(
    pairDetails: ForexPairDetails,
    modifier: Modifier = Modifier,
) {
    val pairDetailsItems = remember(pairDetails) {
        listOf(
            ForexDetailsListItem(stringResource(R.string.list_item_spread), pairDetails.spread.toString()),
            ForexDetailsListItem(stringResource(R.string.list_item_bid), pairDetails.bid.formattedAmount),
            ForexDetailsListItem(stringResource(R.string.list_item_ask), pairDetails.ask.formattedAmount),
            ForexDetailsListItem(stringResource(R.string.list_item_open_24h), pairDetails.open24h.formattedAmount),
            ForexDetailsListItem(stringResource(R.string.list_item_high_24h), pairDetails.high24h.formattedAmount),
            ForexDetailsListItem(stringResource(R.string.list_item_low_24h), pairDetails.low24h.formattedAmount),
            ForexDetailsListItem(stringResource(R.string.list_item_volume_24h), pairDetails.volume24h.toString()),
            ForexDetailsListItem(stringResource(R.string.list_item_pip_value), pairDetails.pipValue.toString())
        )
    }

    Surface(shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pairDetailsItems.forEachIndexed { index, item ->
                    if (index != 0) HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = item.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(12.dp))
                        Text(text = item.value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private data class ForexDetailsListItem(val name: String, val value: String)

@Preview
@PreviewWrapper(wrapper = AppPreviewWrapper::class)
@Composable
private fun ForexMarketStatsCardPreview() {
    ForexMarketStatsCard(
        pairDetails = ForexPairDetails(
            symbol = "EURUSD", name = "Euro / US Dollar", description = "The EUR/USD.",
            currentPrice = Price("1.08452"), bid = Price("1.08450"), ask = Price("1.08454"),
            spread = 0.4, open24h = Price("1.08200"), high24h = Price("1.08600"),
            low24h = Price("1.08100"), volume24h = 150000.0, pipValue = 10.0, isJpyPair = false
        )
    )
}
