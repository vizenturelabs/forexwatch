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

        val symbols = symbolStr.split("/")
        if (symbols.size != 2) return null

        val fromSymbol = symbols[0]
        val toSymbol = symbols[1]
        val isJpyPair = toSymbol == "JPY" || fromSymbol == "JPY"

        val pipMultiplier = if (isJpyPair) BigDecimal(100) else BigDecimal(10000)

        // OPTION 3: Typical spread approximation based on the specific currency pair
        val typicalSpreadPips = getTypicalSpreadPips(fromSymbol, toSymbol)
        val spreadInPrice = BigDecimal(typicalSpreadPips).divide(pipMultiplier, 6, RoundingMode.HALF_EVEN)

        // Derive realistic Bid and Ask prices from the mid-price (currentPrice)
        val halfSpread = spreadInPrice.divide(BigDecimal(2), 6, RoundingMode.HALF_EVEN)
        val bid = currentPrice.subtract(halfSpread)
        val ask = currentPrice.add(halfSpread)

        val open = apiModel.open?.toBigDecimalOrNull() ?: currentPrice
        val high = apiModel.high?.toBigDecimalOrNull() ?: currentPrice
        val low = apiModel.low?.toBigDecimalOrNull() ?: currentPrice

        // Handle the name field cleanly without triggering elvis warnings if it's non-nullable
        val pairName = apiModel.name?.takeIf { it.isNotBlank() } ?: "$fromSymbol / $toSymbol"

        return ForexPairDetails(
            symbol = "$fromSymbol$toSymbol",
            name = pairName,
            description = "Real-time exchange rate for $fromSymbol to $toSymbol",
            currentPrice = Price(currentPrice.toPlainString(), currency, isJpyPair),
            bid = Price(bid.toPlainString(), currency, isJpyPair),
            ask = Price(ask.toPlainString(), currency, isJpyPair),
            spread = typicalSpreadPips,
            open24h = Price(open.toPlainString(), currency, isJpyPair),
            high24h = Price(high.toPlainString(), currency, isJpyPair),
            low24h = Price(low.toPlainString(), currency, isJpyPair),
            volume24h = 0.0,
            pipValue = 10.0,
            isJpyPair = isJpyPair
        )
    }

    private fun getTypicalSpreadPips(fromSymbol: String, toSymbol: String): Double {
        return when (fromSymbol) {
            "EUR" -> when (toSymbol) {
                "USD" -> 1.2
                "GBP", "JPY" -> 1.8
                else -> 2.5
            }
            "GBP" -> when (toSymbol) {
                "USD" -> 1.5
                "JPY" -> 2.5
                else -> 2.5
            }
            "USD" -> when (toSymbol) {
                "JPY" -> 1.2
                "CHF" -> 1.5
                "CAD" -> 1.8
                else -> 2.5
            }
            "AUD" -> if (toSymbol == "USD") 1.4 else 2.5
            "NZD" -> if (toSymbol == "USD") 1.8 else 2.5
            else -> 2.5
        }
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
