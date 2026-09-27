package com.vizenture.forexwatch.data.mapper

import com.vizenture.forexwatch.common.toSanitisedBigDecimalOrNull
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.model.ForexChartApiModel
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.OhlcEntry
import com.vizenture.forexwatch.model.Percentage
import kotlinx.collections.immutable.toPersistentList
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import javax.inject.Inject

class ForexChartMapper @Inject constructor() {

    fun mapApiModelToModel(
        apiModel: ForexChartApiModel,
        currency: Currency,
    ): ForexPairChart {
        val validEntries = apiModel.candles
            .mapNotNull { candle ->
                val open = candle.mid.open.toSanitisedBigDecimalOrNull() ?: return@mapNotNull null
                val high = candle.mid.high.toSanitisedBigDecimalOrNull() ?: return@mapNotNull null
                val low = candle.mid.low.toSanitisedBigDecimalOrNull() ?: return@mapNotNull null
                val close = candle.mid.close.toSanitisedBigDecimalOrNull() ?: return@mapNotNull null
                
                val timestamp = runCatching {
                    Instant.parse(candle.time).epochSecond
                }.getOrNull() ?: return@mapNotNull null

                OhlcEntry(
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    timestamp = timestamp
                )
            }
            .toPersistentList()

        val percentageChange = calculatePercentageChange(validEntries)

        return ForexPairChart(
            currency = currency,
            priceHistory = validEntries,
            periodPriceChangePercentage = percentageChange
        )
    }

    private fun calculatePercentageChange(entries: List<OhlcEntry>): Percentage {
        if (entries.isEmpty()) return Percentage("0.00")
        val first = entries.first().open
        val last = entries.last().close
        if (first == BigDecimal.ZERO) return Percentage("0.00")
        
        val change = ((last - first) / first) * BigDecimal(100)
        return Percentage(change.setScale(2, RoundingMode.HALF_EVEN).toString())
    }
}
