package com.vizenture.forexwatch.ui.component

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.vizenture.forexwatch.model.OhlcEntry
import kotlinx.collections.immutable.ImmutableList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CandlestickChart(
    priceHistory: ImmutableList<OhlcEntry>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val gridColor = MaterialTheme.colorScheme.outlineVariant.toArgb()

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        factory = { context ->
            CandleStickChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setDrawGridBackground(false)
                setDrawBorders(false)
                setNoDataText("No chart data available")
                setNoDataTextColor(textColor)

                xAxis.apply {
                    setPosition(XAxis.XAxisPosition.BOTTOM)
                    setDrawGridLines(false)
                    setTextColor(textColor)

                    // Use the explicit setter to avoid unresolved reference errors
                    setGranularityEnabled(true)
                    granularity = 60f
                    setLabelCount(5, true)

                    val minTimestamp = priceHistory.minOfOrNull { it.timestamp } ?: 0L
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            // Shift back to original timestamp and convert to milliseconds
                            val actualTimestamp = (value.toLong() + minTimestamp) * 1000L
                            return dateFormat.format(Date(actualTimestamp))
                        }
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    setGridColor(gridColor)
                    setTextColor(textColor)
                    setDrawAxisLine(false)
                    setSpaceTop(10f)
                    setSpaceBottom(10f)
                }

                axisRight.isEnabled = false

                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
            }
        },
        update = { chart ->
            if (priceHistory.isEmpty()) {
                chart.clear()
                return@AndroidView
            }

            // CRITICAL FIX: Shift timestamps to start from 0.
            // MPAndroidChart suffers from floating-point precision loss in its ViewPortHandler matrix
            // when X values are large numbers (like epoch seconds: 1,700,000,000).
            // This causes the chart to render incorrectly or remain completely blank.
            val minTimestamp = priceHistory.minOf { it.timestamp }
            val entries = priceHistory.map { entry ->
                CandleEntry(
                    (entry.timestamp - minTimestamp).toFloat(), // Shifted X coordinate
                    entry.high.toFloat(),
                    entry.low.toFloat(),
                    entry.open.toFloat(),
                    entry.close.toFloat()
                )
            }

            val dataSet = CandleDataSet(entries, "Price").apply {
                axisDependency = com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT
                color = textColor
                shadowColor = Color.DKGRAY
                shadowWidth = 1.5f
                decreasingColor = Color.RED
                decreasingPaintStyle = Paint.Style.FILL
                increasingColor = primaryColor
                increasingPaintStyle = Paint.Style.FILL
                setDrawValues(false)
            }

            chart.data = CandleData(dataSet)
            chart.invalidate()
            // Removed animateX(500) to prevent animation jank during Jetpack Compose recompositions
        }
    )
}
