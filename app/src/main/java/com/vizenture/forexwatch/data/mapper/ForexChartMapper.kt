package com.vizenture.forexwatch.data.mapper

import com.google.gson.JsonObject
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageExchangeRateResponse
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
        apiModel: JsonObject,
        currency: Currency
    ): ForexPairChart {
        // CRITICAL: Check for Alpha Vantage error messages
        val errorMessage = apiModel.get("Note")?.asString
            ?: apiModel.get("Error Message")?.asString
            ?: apiModel.get("Information")?.asString

        if (errorMessage != null) {
            throw Exception("Alpha Vantage API Error: $errorMessage")
        }

        val timeSeriesKey = apiModel.keySet().firstOrNull { it.startsWith("Time Series FX") }
            ?: throw Exception("Invalid API response: Time Series FX key not found")

        val timeSeriesObject = apiModel.getAsJsonObject(timeSeriesKey)
            ?: throw Exception("Invalid API response: Time Series object is null")

        val ohlcEntries = timeSeriesObject.entrySet().mapNotNull { (timestampStr, value) ->
            val candle = value.asJsonObject
            val open = candle.get("1. open")?.asString?.toBigDecimalOrNull() ?: return@mapNotNull null
            val high = candle.get("2. high")?.asString?.toBigDecimalOrNull() ?: return@mapNotNull null
            val low = candle.get("3. low")?.asString?.toBigDecimalOrNull() ?: return@mapNotNull null
            val close = candle.get("4. close")?.asString?.toBigDecimalOrNull() ?: return@mapNotNull null

            // FIX: Daily keys lack a time component, causing LocalDateTime.parse to fail and drop all daily entries.
            // We must use LocalDate.parse() for daily data and convert it to the start of the day.
            val timestamp = runCatching {
                if (timestampStr.contains(" ")) {
                    LocalDateTime.parse(timestampStr, timeFormatter).toInstant(ZoneOffset.UTC).epochSecond
                } else {
                    LocalDate.parse(timestampStr, dailyTimeFormatter).atStartOfDay(ZoneOffset.UTC).toEpochSecond()
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

    fun mapExchangeRateToDetails(
        apiModel: AlphaVantageExchangeRateResponse,
        currency: Currency
    ): ForexPairDetails? {
        val rateData = apiModel.exchangeRate ?: return null

        val bid = rateData.bidPrice.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val ask = rateData.askPrice.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val currentPrice = rateData.rate.toBigDecimalOrNull() ?: BigDecimal.ZERO

        val isJpyPair = rateData.toSymbol == "JPY" || rateData.fromSymbol == "JPY"
        val pipMultiplier = if (isJpyPair) BigDecimal(100) else BigDecimal(10000)
        val spread = (ask - bid) * pipMultiplier

        return ForexPairDetails(
            symbol = "${rateData.fromSymbol}${rateData.toSymbol}",
            name = "${rateData.fromName} / ${rateData.toName}",
            description = "Real-time exchange rate for ${rateData.fromSymbol} to ${rateData.toSymbol}",
            currentPrice = Price(currentPrice.toPlainString(), currency, isJpyPair),
            bid = Price(bid.toPlainString(), currency, isJpyPair),
            ask = Price(ask.toPlainString(), currency, isJpyPair),
            spread = spread.setScale(1, RoundingMode.HALF_EVEN).toDouble(),
            open24h = Price(currentPrice.toPlainString(), currency, isJpyPair),
            high24h = Price(currentPrice.toPlainString(), currency, isJpyPair),
            low24h = Price(currentPrice.toPlainString(), currency, isJpyPair),
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
