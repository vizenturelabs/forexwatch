package com.vizenture.forexwatch.data.mapper

import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataQuoteResponse
import com.vizenture.forexwatch.data.source.remote.model.TwelveDataTimeSeriesResponse
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.ForexPairDetails
import com.vizenture.forexwatch.model.OhlcEntry
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.model.Price
import kotlinx.collections.immutable.toPersistentList
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ForexChartMapper @Inject constructor() {

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val dailyTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun mapTimeSeriesToChart(
        apiModel: TwelveDataTimeSeriesResponse,
        currency: Currency
    ): ForexPairChart {
        if (apiModel.status != "ok" || apiModel.values.isNullOrEmpty()) {
            val errorMsg = apiModel.message ?: "Unknown API error"
            throw Exception("Twelve Data API Error: $errorMsg")
        }

        val ohlcEntries = apiModel.values.mapNotNull { candle ->
            val open = candle.open.toBigDecimalOrNull() ?: return@mapNotNull null
            val high = candle.high.toBigDecimalOrNull() ?: return@mapNotNull null
            val low = candle.low.toBigDecimalOrNull() ?: return@mapNotNull null
            val close = candle.close.toBigDecimalOrNull() ?: return@mapNotNull null

            val timestamp = runCatching {
                if (candle.datetime.contains(" ")) {
                    LocalDateTime.parse(candle.datetime, timeFormatter).toInstant(ZoneOffset.UTC).epochSecond
                } else {
                    LocalDate.parse(candle.datetime, dailyTimeFormatter).atStartOfDay(ZoneOffset.UTC).toEpochSecond()
                }
            }.getOrNull() ?: return@mapNotNull null

            OhlcEntry(
                open = open,
                high = high,
                low = low,
                close = close,
                timestamp = timestamp
            )
        }.sortedBy { it.timestamp }.toPersistentList()

        if (ohlcEntries.isEmpty()) {
            throw Exception("No valid OHLC data found in API response")
        }

        val percentageChange = calculatePercentageChange(ohlcEntries)

        return ForexPairChart(
            currency = currency,
            priceHistory = ohlcEntries,
            periodPriceChangePercentage = percentageChange
        )
    }

    fun mapQuoteToPairDetails(
        apiModel: TwelveDataQuoteResponse,
        currency: Currency
    ): ForexPairDetails? {
        // Validate required fields early to allow smart-casting to non-null
        val closeStr = apiModel.close ?: return null
        val symbolStr = apiModel.symbol ?: return null

        if (apiModel.status == "error") return null

        val currentPrice = closeStr.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val open = apiModel.open?.toBigDecimalOrNull() ?: currentPrice
        val high = apiModel.high?.toBigDecimalOrNull() ?: currentPrice
        val low = apiModel.low?.toBigDecimalOrNull() ?: currentPrice

        // Twelve Data free tier quote endpoint doesn't provide Bid/Ask.
        // Spread is effectively 0.0 since we only have the mid-price (close).
        val spread = 0.0

        val symbols = symbolStr.split("/")
        if (symbols.size != 2) return null

        val fromSymbol = symbols[0]
        val toSymbol = symbols[1]
        val isJpyPair = toSymbol == "JPY" || fromSymbol == "JPY"

        // Handle the name field cleanly without triggering elvis warnings if it's non-nullable
        val pairName = apiModel.name?.takeIf { it.isNotBlank() } ?: "$fromSymbol / $toSymbol"

        return ForexPairDetails(
            symbol = "$fromSymbol$toSymbol",
            name = pairName,
            description = "Real-time exchange rate for $fromSymbol to $toSymbol",
            currentPrice = Price(currentPrice.toPlainString(), currency, isJpyPair),
            // Inlined currentPrice directly to resolve the "variable is same" warning
            bid = Price(currentPrice.toPlainString(), currency, isJpyPair),
            ask = Price(currentPrice.toPlainString(), currency, isJpyPair),
            spread = spread,
            open24h = Price(open.toPlainString(), currency, isJpyPair),
            high24h = Price(high.toPlainString(), currency, isJpyPair),
            low24h = Price(low.toPlainString(), currency, isJpyPair),
            volume24h = 0.0,
            pipValue = 10.0,
            isJpyPair = isJpyPair
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
