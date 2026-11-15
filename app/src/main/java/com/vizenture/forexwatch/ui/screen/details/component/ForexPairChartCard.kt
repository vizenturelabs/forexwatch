package com.vizenture.forexwatch.ui.screen.details.component

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.ForexPairDetails
import com.vizenture.forexwatch.model.OhlcEntry
import com.vizenture.forexwatch.ui.component.CandlestickChart
import com.vizenture.forexwatch.ui.component.IndicatorPoint
import com.vizenture.forexwatch.ui.component.PercentageChangeChip
import com.vizenture.forexwatch.ui.model.ChartPeriod
import com.vizenture.forexwatch.ui.screen.details.TechnicalIndicator
import kotlinx.collections.immutable.ImmutableList
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneOffset
import kotlin.math.sqrt

// Custom modifier to perfectly rotate and map content to landscape dimensions
fun Modifier.rotateLandscape(): Modifier = this.layout { measurable, constraints ->
    val childConstraints = Constraints(
        minWidth = constraints.minHeight,
        maxWidth = constraints.maxHeight,
        minHeight = constraints.minWidth,
        maxHeight = constraints.maxWidth
    )
    val placeable = measurable.measure(childConstraints)

    layout(constraints.maxWidth, constraints.maxHeight) {
        placeable.placeWithLayer(0, 0) {
            rotationZ = 90f
            transformOrigin = TransformOrigin(0f, 0f)
            translationX = constraints.maxWidth.toFloat()
        }
    }
}

@Composable
fun ForexPairChartCard(
    pairDetails: ForexPairDetails,
    pairChart: ForexPairChart,
    chartPeriod: ChartPeriod,
    onClickChartPeriod: (ChartPeriod) -> Unit,
    selectedIndicator: TechnicalIndicator,
    onSelectIndicator: (TechnicalIndicator) -> Unit,
    indicatorData: List<IndicatorPoint>,
    modifier: Modifier = Modifier,
) {
    var isFullScreen by remember { mutableStateOf(false) }

    Surface(shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {

            // Price header
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = pairDetails.currentPrice.formattedAmount,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(4.dp))

                // Single row: Percentage + Period label (left) | Indicator selector (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    // Push indicator selector to the right side of this row
                    Spacer(Modifier.weight(1f))

                    IndicatorSelector(
                        selectedIndicator = selectedIndicator,
                        onSelectIndicator = onSelectIndicator
                    )
                }
            }

            // No spacer — ChartPeriodSelector sits immediately below the percentage row
            ChartPeriodSelector(
                selectedChartPeriod = chartPeriod,
                onChartPeriodSelected = onClickChartPeriod,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Chart
            Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                if (pairChart.priceHistory.isNotEmpty()) {
                    CandlestickChart(
                        priceHistory = pairChart.priceHistory,
                        indicatorData = indicatorData,
                        onFullScreenClick = { isFullScreen = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.empty_chart_message),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            MarketStatsGrid(
                pairDetails = pairDetails,
                pairChart = pairChart,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }

    if (isFullScreen) {
        FullScreenChartDialog(
            priceHistory = pairChart.priceHistory,
            indicatorData = indicatorData,
            onDismiss = { isFullScreen = false }
        )
    }
}

@Composable
fun MarketStatsGrid(
    pairDetails: ForexPairDetails,
    pairChart: ForexPairChart,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales.get(0)
    val isJpy = pairDetails.isJpyPair
    val pipDivisor = if (isJpy) BigDecimal(100) else BigDecimal(10000)

    // --- Category 2: Calculated Series Analytics ---
    val periodHigh = pairChart.priceHistory.maxOfOrNull { it.high } ?: BigDecimal.ZERO
    val periodLow = pairChart.priceHistory.minOfOrNull { it.low } ?: BigDecimal.ZERO
    val periodOpen = pairChart.priceHistory.firstOrNull()?.open ?: BigDecimal.ZERO
    val currentClose = pairChart.priceHistory.lastOrNull()?.close ?: BigDecimal.ZERO

    val periodRange = periodHigh.subtract(periodLow)
    val periodRangePips = periodRange.divide(pipDivisor, 1, RoundingMode.HALF_UP)

    val distanceFromOpen = currentClose.subtract(periodOpen)
    val distanceFromOpenPips = distanceFromOpen.divide(pipDivisor, 1, RoundingMode.HALF_UP)

    val midpoint = periodHigh.add(periodLow).divide(BigDecimal(2), 6, RoundingMode.HALF_UP)
    val midpointDiff = currentClose.subtract(midpoint)
    val midpointLabel = when {
        midpointDiff > BigDecimal.ZERO -> "Above Midpoint"
        midpointDiff < BigDecimal.ZERO -> "Below Midpoint"
        else -> "At Midpoint"
    }

    // --- Category 3: Advanced Forex-Specific Derivatives ---
    // ADR: Average of (High - Low) across all candles in the period
    val adrValue = if (pairChart.priceHistory.isNotEmpty()) {
        val totalRange = pairChart.priceHistory.sumOf { it.high.subtract(it.low) }
        totalRange.divide(BigDecimal(pairChart.priceHistory.size), 6, RoundingMode.HALF_UP)
    } else BigDecimal.ZERO
    val adrPips = adrValue.divide(pipDivisor, 1, RoundingMode.HALF_UP)

    // Pivot Points: Calculated from the first candle as "previous period" reference
    val pivotHigh = pairChart.priceHistory.firstOrNull()?.high ?: BigDecimal.ZERO
    val pivotLow = pairChart.priceHistory.firstOrNull()?.low ?: BigDecimal.ZERO
    val pivotClose = pairChart.priceHistory.firstOrNull()?.close ?: BigDecimal.ZERO
    val pivotPoint = pivotHigh.add(pivotLow).add(pivotClose).divide(BigDecimal(3), 6, RoundingMode.HALF_UP)

    // Volatility: Standard Deviation of close prices
    val volatility = if (pairChart.priceHistory.size > 1) {
        val closes = pairChart.priceHistory.map { it.close.toDouble() }
        val mean = closes.average()
        val variance = closes.sumOf { (it - mean) * (it - mean) } / closes.size
        sqrt(variance)
    } else 0.0

    // --- Category 4: Market Session Context ---
    val sessionInfo = getCurrentForexSession()

    Column(modifier = modifier) {

        // ==========================================
        // CATEGORY 1: QUOTE DATA
        // ==========================================
        SectionHeader(title = "Quote Data")
        Spacer(Modifier.height(4.dp))
        StatRow(
            label = "Previous Close",
            value = pairDetails.previousClose.formattedAmount
        )
        StatRow(
            label = "Absolute Change",
            value = pairDetails.absoluteChange
        )
        StatRow(
            label = "Last Update",
            value = pairDetails.lastUpdateTime
        )

        Spacer(Modifier.height(16.dp))

        // ==========================================
        // CATEGORY 2: PERIOD ANALYTICS
        // ==========================================
        SectionHeader(title = "Period Analytics")
        Spacer(Modifier.height(4.dp))
        StatRow(
            label = "Period Range",
            value = "$periodRangePips pips"
        )
        StatRow(
            label = "Distance from Open",
            value = "$distanceFromOpenPips pips"
        )
        StatRow(
            label = "Midpoint Analysis",
            value = midpointLabel
        )

        Spacer(Modifier.height(16.dp))

        // ==========================================
        // CATEGORY 3: FOREX DERIVATIVES
        // ==========================================
        SectionHeader(title = "Forex Derivatives")
        Spacer(Modifier.height(4.dp))
        StatRow(
            label = "Avg Range (ADR)",
            value = "$adrPips pips"
        )
        StatRow(
            label = "Pivot Point",
            value = pivotPoint.setScale(if (isJpy) 3 else 5, RoundingMode.HALF_UP).toPlainString()
        )
        StatRow(
            label = "Volatility (σ)",
            value = String.format(locale, "%.4f", volatility)
        )

        Spacer(Modifier.height(16.dp))

        // ==========================================
        // CATEGORY 4: MARKET SESSION
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Market Session",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = sessionInfo.countryCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(4.dp))
        StatRow(
            label = "Current Session",
            value = sessionInfo.sessionName
        )
    }
}

// ==========================================
// HELPER COMPOSABLES
// ==========================================

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ==========================================
// MARKET SESSION LOGIC
// ==========================================

data class ForexSession(
    val sessionName: String,
    val countryCode: String
)

fun getCurrentForexSession(): ForexSession {
    val nowUtc = Instant.now().atZone(ZoneOffset.UTC)
    val hour = nowUtc.hour
    val day = nowUtc.dayOfWeek

    if (day == java.time.DayOfWeek.SATURDAY || day == java.time.DayOfWeek.SUNDAY) {
        return ForexSession(sessionName = "Market Closed", countryCode = "--")
    }

    return when (hour) {
        in 13..16 -> ForexSession(
            sessionName = "London / New York Overlap",
            countryCode = "GB / US"
        )
        8 -> ForexSession(
            sessionName = "Asia / London Overlap",
            countryCode = "JP / GB"
        )
        in 9..12 -> ForexSession(
            sessionName = "London Session",
            countryCode = "GB"
        )
        in 17..21 -> ForexSession(
            sessionName = "New York Session",
            countryCode = "US"
        )
        else -> ForexSession(
            sessionName = "Asian Session",
            countryCode = "JP"
        )
    }
}

@Composable
fun IndicatorSelector(
    selectedIndicator: TechnicalIndicator,
    onSelectIndicator: (TechnicalIndicator) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Indicator: ${selectedIndicator.displayName}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TechnicalIndicator.entries.forEach { indicator ->
                DropdownMenuItem(
                    text = { Text(indicator.displayName) },
                    onClick = {
                        onSelectIndicator(indicator)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FullScreenChartDialog(
    priceHistory: ImmutableList<OhlcEntry>,
    indicatorData: List<IndicatorPoint>,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (isPortrait) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotateLandscape()
                ) {
                    CandlestickChart(
                        priceHistory = priceHistory,
                        indicatorData = indicatorData,
                        onFullScreenClick = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CandlestickChart(
                        priceHistory = priceHistory,
                        indicatorData = indicatorData,
                        onFullScreenClick = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
