package com.vizenture.forexwatch.ui.screen.details.component

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.Price
import com.vizenture.forexwatch.ui.component.CandlestickChart
import com.vizenture.forexwatch.ui.component.PercentageChangeChip
import com.vizenture.forexwatch.ui.model.ChartPeriod

@Composable
fun ForexPairChartCard(
    currentPrice: Price,
    pairChart: ForexPairChart,
    chartPeriod: ChartPeriod,
    onClickChartPeriod: (ChartPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(text = currentPrice.formattedAmount, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PercentageChangeChip(
                        percentage = pairChart.periodPriceChangePercentage,
                        modifier = Modifier.animateContentSize()
                    )
                    Spacer(Modifier.width(8.dp))
                    if (pairChart.priceHistory.isNotEmpty()) {
                        Text(
                            text = stringResource(chartPeriod.longNameId),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            if (pairChart.priceHistory.isNotEmpty()) {
                CandlestickChart(
                    priceHistory = pairChart.priceHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(horizontal = 12.dp)
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(240.dp)) {
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

