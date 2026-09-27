package com.vizenture.forexwatch.data.mapper

import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.model.ForexChartApiModel
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.OhlcEntry
import com.vizenture.forexwatch.model.Percentage
import kotlinx.collections.immutable.toImmutableList
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

class ForexChartMapper {
    fun toDomain(apiModel: ForexChartApiModel, currency: Currency): ForexPairChart {
        val ohlcEntries = apiModel.candles.map { candle ->
            OhlcEntry(
                open = candle.mid.open.toBigDecimal(),
                high = candle.mid.high.toBigDecimal(),
                low = candle.mid.low.toBigDecimal(),
                close = candle.mid.close.toBigDecimal(),
                timestamp = Instant.parse(candle.time).toEpochMilli()
            )
        }.toImmutableList()

        val percentageChange = calculatePercentageChange(ohlcEntries)

        return ForexPairChart(
            currency = currency,
            priceHistory = ohlcEntries,
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
