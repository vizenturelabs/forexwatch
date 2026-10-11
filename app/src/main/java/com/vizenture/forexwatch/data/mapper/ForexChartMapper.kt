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
        // Ensure required fields are present
        if (apiModel.status != "ok" || apiModel.close == null || apiModel.symbol == null) return null

        // FIX: Use safe calls (?.) for all nullable String properties
        val currentPrice = apiModel.close?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val open = apiModel.open?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val high = apiModel.high?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val low = apiModel.low?.toBigDecimalOrNull() ?: BigDecimal.ZERO

        // Twelve Data free tier doesn't explicitly provide Bid/Ask in the quote endpoint.
        // We approximate them using the current price.
        val bid = currentPrice
        val ask = currentPrice

        // Safely unwrap symbol and split it
        val symbolStr = apiModel.symbol ?: return null
        val symbols = symbolStr.split("/")
        if (symbols.size != 2) return null

        val fromSymbol = symbols[0]
        val toSymbol = symbols[1]
        val isJpyPair = toSymbol == "JPY" || fromSymbol == "JPY"

        val pipMultiplier = if (isJpyPair) BigDecimal(100) else BigDecimal(10000)
        val spread = (ask - bid) * pipMultiplier

        return ForexPairDetails(
            symbol = "$fromSymbol$toSymbol",
            name = apiModel.instrumentName ?: "$fromSymbol / $toSymbol",
            description = "Real-time exchange rate for $fromSymbol to $toSymbol",
            currentPrice = Price(currentPrice.toPlainString(), currency, isJpyPair),
            bid = Price(bid.toPlainString(), currency, isJpyPair),
            ask = Price(ask.toPlainString(), currency, isJpyPair),
            spread = spread.setScale(1, RoundingMode.HALF_EVEN).toDouble(),
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
