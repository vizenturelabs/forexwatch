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
                    setPosition(XAxis.XAxisPosition.BOTTOM) // Fixed: use setPosition()
                    setDrawGridLines(false)
                    setTextColor(textColor)                 // Fixed: use setTextColor()
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            return dateFormat.format(Date(value.toLong()))
                        }
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    setGridColor(gridColor)                 // Fixed: use setGridColor()
                    setTextColor(textColor)                 // Fixed: use setTextColor()
                    setDrawAxisLine(false)
                    setSpaceTop(10f)                        // Fixed: use setSpaceTop()
                    setSpaceBottom(10f)                     // Fixed: use setSpaceBottom()
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

            val entries = priceHistory.mapIndexed { index, entry ->
                CandleEntry(
                    index.toFloat(),
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
            chart.animateX(500)
        }
    )
}
