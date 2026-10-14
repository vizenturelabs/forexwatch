package com.vizenture.forexwatch.ui.component

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
    // FIX: Renamed local variables to avoid shadowing MPAndroidChart properties
    val primaryArgb = MaterialTheme.colorScheme.primary.toArgb()
    val textArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val gridArgb = MaterialTheme.colorScheme.outlineVariant.toArgb()

    // FIX: Use optimized state delegates for primitive types
    var currentMinTs by remember { mutableLongStateOf(0L) }
    var previousEntryCount by remember { mutableIntStateOf(0) }

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
                setNoDataText("Loading chart...")
                setNoDataTextColor(textArgb)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    // FIX: Reverted to explicit setters for methods without Kotlin synthetic properties
                    setDrawGridLines(false)
                    textColor = textArgb
                    setGranularityEnabled(true)
                    granularity = 60f
                    setLabelCount(5, true)

                    valueFormatter = object : ValueFormatter() {
                        private val dateTimeFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                        override fun getFormattedValue(value: Float): String {
                            val actualTimestamp = (value.toLong() + currentMinTs) * 1000L
                            val date = Date(actualTimestamp)
                            val cal = java.util.Calendar.getInstance().apply { time = date }

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
                    // FIX: Property access syntax now works perfectly without variable shadowing
                    gridColor = gridArgb
                    textColor = textArgb
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

            val newMinTs = priceHistory.minOf { it.timestamp }

            // OPTIMIZATION: Skip redraw if the data hasn't actually changed
            if (priceHistory.size == previousEntryCount && currentMinTs == newMinTs && chart.data != null) {
                return@AndroidView
            }

            previousEntryCount = priceHistory.size
            currentMinTs = newMinTs

            val entries = priceHistory.map { entry ->
                CandleEntry(
                    (entry.timestamp - newMinTs).toFloat(),
                    entry.high.toFloat(),
                    entry.low.toFloat(),
                    entry.open.toFloat(),
                    entry.close.toFloat()
                )
            }

            // OPTIMIZATION: Update existing dataset in-place to prevent flashing and viewport reset
            val existingData = chart.data
            if (existingData != null && existingData.dataSetCount > 0) {
                val dataSet = existingData.getDataSetByIndex(0) as CandleDataSet

                // Save current zoom and scroll position before modifying data
                val currentScaleX = chart.viewPortHandler.scaleX
                val currentLowestVisibleX = chart.lowestVisibleX

                dataSet.clear()
                for (entry in entries) {
                    dataSet.addEntry(entry)
                }

                chart.data.notifyDataChanged()
                chart.notifyDataSetChanged()
                chart.invalidate()

                // Restore zoom and scroll position so the user doesn't lose their place
                if (currentScaleX > 1f) {
                    chart.zoom(
                        currentScaleX,
                        chart.viewPortHandler.scaleY,
                        currentLowestVisibleX,
                        0f,
                        YAxis.AxisDependency.LEFT
                    )
                    chart.moveViewToX(currentLowestVisibleX)
                }
            } else {
                // Initial load: Create the dataset from scratch
                val dataSet = CandleDataSet(entries, "Price").apply {
                    axisDependency = YAxis.AxisDependency.LEFT
                    color = textArgb
                    shadowColor = Color.DKGRAY
                    shadowWidth = 1.5f
                    decreasingColor = Color.RED
                    decreasingPaintStyle = Paint.Style.FILL
                    increasingColor = primaryArgb
                    increasingPaintStyle = Paint.Style.FILL
                    // FIX: Reverted to explicit setter
                    setDrawValues(false)
                }
                chart.data = CandleData(dataSet)
                chart.invalidate()
            }
        }
    )
}
