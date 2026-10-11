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

    // FIX: Use a var to hold the minimum timestamp so the ValueFormatter (created in factory)
    // can read the updated value from the update block.
    var currentMinTs = 0L

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

                    setGranularityEnabled(true)
                    granularity = 60f
                    setLabelCount(5, true)

                    valueFormatter = object : ValueFormatter() {
                        private val dateTimeFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        override fun getFormattedValue(value: Float): String {
                            val actualTimestamp = (value.toLong() + currentMinTs) * 1000L
                            val date = Date(actualTimestamp)

                            // FIX: Replaced deprecated date.hours and date.minutes with Calendar
                            val cal = java.util.Calendar.getInstance().apply { time = date }

                            // If the time is exactly midnight (00:00), it's daily data. Show full date.
                            // Otherwise, show month-day and time for intraday data.
                            return if (cal.get(java.util.Calendar.HOUR_OF_DAY) == 0 && cal.get(java.util.Calendar.MINUTE) == 0) {
                                dateFormat.format(date)
                            } else {
                                dateTimeFormat.format(date)
                            }
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

            // Update the captured variable so the X-axis formatter knows the correct base timestamp
            currentMinTs = priceHistory.minOf { it.timestamp }

            val entries = priceHistory.map { entry ->
                CandleEntry(
                    (entry.timestamp - currentMinTs).toFloat(), // Shifted X coordinate
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
        }
    )
}
