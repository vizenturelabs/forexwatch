package com.vizenture.forexwatch.ui.screen.details.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.model.Price
import com.vizenture.forexwatch.model.PriceEntry
import com.vizenture.forexwatch.model.ScrubData
import com.vizenture.forexwatch.ui.component.PercentageChangeChip
import com.vizenture.forexwatch.ui.component.ScrubPriceGraph
import com.vizenture.forexwatch.ui.model.ChartPeriod
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.vizenture.forexwatch.ui.preview.AppPreviewWrapper
import kotlinx.collections.immutable.persistentListOf
import java.math.BigDecimal

@Composable
fun ForexPairChartCard(
    currentPrice: Price,
    pairChart: ForexPairChart,
    chartPeriod: ChartPeriod,
    onClickChartPeriod: (ChartPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    var scrubData by remember { mutableStateOf<ScrubData?>(null) }

    val displayPrice = scrubData?.let { Price(price = it.price.toPlainString(), currency = pairChart.currency) } ?: currentPrice
    val displayPercentage = scrubData?.changePercentage ?: pairChart.periodPriceChangePercentage

    Surface(shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(text = displayPrice.formattedAmount, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PercentageChangeChip(percentage = displayPercentage, modifier = Modifier.animateContentSize())
                    Spacer(Modifier.width(8.dp))
                    if (pairChart.priceHistory.isNotEmpty() && scrubData == null) {
                        Text(text = stringResource(chartPeriod.longNameId), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            if (pairChart.priceHistory.isNotEmpty()) {
                ScrubPriceGraph(
                    priceHistory = pairChart.priceHistory,
                    priceChangePercentage = pairChart.periodPriceChangePercentage,
                    isGraphAnimated = true,
                    onScrub = { scrubData = it },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Text(text = stringResource(R.string.empty_chart_message), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleSmall)
                }
            }

            Spacer(Modifier.height(12.dp))

            ChartPeriodSelector(
                selectedChartPeriod = chartPeriod,
                onChartPeriodSelected = onClickChartPeriod,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Preview
@PreviewWrapper(wrapper = AppPreviewWrapper::class)
@Composable
private fun ForexPairChartCardPreview() {
    var chartPeriod by remember { mutableStateOf(ChartPeriod.Day) }
    ForexPairChartCard(
        currentPrice = Price("1.08452"),
        pairChart = ForexPairChart(
            currency = Currency.USD,
            priceHistory = persistentListOf(PriceEntry(BigDecimal("1.08550"), 1700000000L, "14 Nov 2023")),
            periodPriceChangePercentage = Percentage("0.15")
        ),
        chartPeriod = chartPeriod,
        onClickChartPeriod = { chartPeriod = it }
    )
}
