package com.vizenture.forexwatch.ui.component

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.vizenture.forexwatch.model.OhlcEntry
import kotlinx.collections.immutable.ImmutableList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class IndicatorPoint(val x: Float, val y: Float)

@Composable
fun CandlestickChart(
    priceHistory: ImmutableList<OhlcEntry>,
    modifier: Modifier = Modifier,
    indicatorData: List<IndicatorPoint> = emptyList(),
    onFullScreenClick: (() -> Unit)? = null
) {
    val bullishColor = Color.rgb(0, 128, 0)
    val bearishColor = Color.rgb(255, 0, 0)
    val indicatorColor = Color.rgb(255, 165, 0)

    val textArgb = MaterialTheme.colorScheme.onSurface.toArgb()
    val gridArgb = MaterialTheme.colorScheme.outlineVariant.toArgb()

    var currentMinTs by remember { mutableLongStateOf(0L) }
    var previousEntryCount by remember { mutableIntStateOf(0) }
    var chartInstance by remember { mutableStateOf<CombinedChart?>(null) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                @Suppress("UsePropertyAccessSyntax")
                CombinedChart(context).apply {
                    chartInstance = this
                    description.isEnabled = false
                    legend.isEnabled = false
                    setDrawGridBackground(false)
                    setDrawBorders(false)
                    setNoDataText("Loading chart...")
                    setNoDataTextColor(textArgb)
                    setPinchZoom(true)
                    setScaleEnabled(true)
                    isDragEnabled = true

                    setDrawOrder(
                        arrayOf(
                            CombinedChart.DrawOrder.CANDLE,
                            CombinedChart.DrawOrder.LINE
                        )
                    )

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
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
                        gridColor = gridArgb
                        textColor = textArgb
                        setDrawAxisLine(false)
                        setSpaceTop(0f)
                        setSpaceBottom(10f)
                    }
                    axisRight.isEnabled = false
                }
            },
            update = { chart ->
                if (priceHistory.isEmpty()) {
                    chart.clear()
                    return@AndroidView
                }

                val newMinTs = priceHistory.minOf { it.timestamp }

                if (priceHistory.size == previousEntryCount && currentMinTs == newMinTs && chart.data != null && indicatorData.isEmpty()) {
                    return@AndroidView
                }

                previousEntryCount = priceHistory.size
                currentMinTs = newMinTs

                val candleEntries = priceHistory.map { entry ->
                    CandleEntry(
                        (entry.timestamp - newMinTs).toFloat(),
                        entry.high.toFloat(), entry.low.toFloat(),
                        entry.open.toFloat(), entry.close.toFloat()
                    )
                }

                val candleDataSet = CandleDataSet(candleEntries, "Price").apply {
                    axisDependency = YAxis.AxisDependency.LEFT
                    shadowWidth = 1.5f
                    shadowColorSameAsCandle = true
                    decreasingColor = bearishColor
                    decreasingPaintStyle = Paint.Style.FILL
                    increasingColor = bullishColor
                    increasingPaintStyle = Paint.Style.FILL
                    setDrawValues(false)
                }

                val combinedData = CombinedData()
                combinedData.setData(CandleData(candleDataSet))

                if (indicatorData.isNotEmpty()) {
                    // FIX: Align indicator x-coordinates with the chart's base timestamp.
                    // indicatorData.x now stores raw timestamps from the ViewModel.
                    val lineEntries = indicatorData.map { Entry((it.x - newMinTs), it.y) }
                    val lineDataSet = LineDataSet(lineEntries, "Indicator").apply {
                        color = indicatorColor
                        setDrawCircles(false)
                        lineWidth = 2f
                        axisDependency = YAxis.AxisDependency.LEFT
                        setDrawValues(false)
                    }
                    combinedData.setData(LineData(lineDataSet))
                }

                chart.data = combinedData
                chart.invalidate()
            }
        )

        if (onFullScreenClick != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { chartInstance?.zoomIn() }) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { chartInstance?.zoomOut() }) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { onFullScreenClick() }) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "Full Screen", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
