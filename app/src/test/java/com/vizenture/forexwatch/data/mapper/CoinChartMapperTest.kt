package com.vizenture.forexwatch.data.mapper

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageTimeSeriesResponse
import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageTimeSeriesResponseAdapter
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.OhlcEntry
import com.vizenture.forexwatch.model.Percentage
import kotlinx.collections.immutable.persistentListOf
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class ForexChartMapperTest {

    private lateinit var forexChartMapper: ForexChartMapper
    private lateinit var gson: Gson

    @Before
    fun setup() {
        forexChartMapper = ForexChartMapper()
        gson = GsonBuilder()
            .registerTypeAdapter(AlphaVantageTimeSeriesResponse::class.java, AlphaVantageTimeSeriesResponseAdapter())
            .create()
    }

    @Test
    fun `When api response is empty should return empty chart`() {
        // Arrange
        val jsonString = "{}"
        val apiModel = gson.fromJson(jsonString, AlphaVantageTimeSeriesResponse::class.java)

        val expectedChart = ForexPairChart(
            currency = Currency.USD,
            priceHistory = persistentListOf(),
            periodPriceChangePercentage = Percentage("0.00")
        )

        // Act
        val chart = forexChartMapper.mapTimeSeriesToChart(apiModel, Currency.USD)

        // Assert
        assertThat(chart).isEqualTo(expectedChart)
    }

    @Test
    fun `When time series key is missing should return empty chart`() {
        // Arrange
        val jsonString = """
            {
              "Meta Data": {
                "1. Information": "Forex Intraday",
                "2. From Symbol": "EUR",
                "3. To Symbol": "USD",
                "4. Last Refreshed": "2023-11-15 10:00:00",
                "5. Interval": "60min"
              },
              "Random Key": { }
            }
        """.trimIndent()
        val apiModel = gson.fromJson(jsonString, AlphaVantageTimeSeriesResponse::class.java)

        val expectedChart = ForexPairChart(
            currency = Currency.USD,
            priceHistory = persistentListOf(),
            periodPriceChangePercentage = Percentage("0.00")
        )

        // Act
        val chart = forexChartMapper.mapTimeSeriesToChart(apiModel, Currency.USD)

        // Assert
        assertThat(chart).isEqualTo(expectedChart)
    }

    @Test
    fun `When valid OHLC data is provided should return valid chart with correct percentage change`() {
        // Arrange
        val jsonString = """
            {
              "Meta Data": {
                "1. Information": "Forex Intraday",
                "2. From Symbol": "EUR",
                "3. To Symbol": "USD",
                "4. Last Refreshed": "2023-11-15 10:00:00",
                "5. Interval": "60min"
              },
              "Time Series FX (60min)": {
                "2023-11-15 10:00:00": {
                  "1. open": "1.2450",
                  "2. high": "1.2500",
                  "3. low": "1.2400",
                  "4. close": "1.2450"
                },
                "2023-11-15 09:00:00": {
                  "1. open": "1.2350",
                  "2. high": "1.2450",
                  "3. low": "1.2300",
                  "4. close": "1.2400"
                }
              }
            }
        """.trimIndent()
        val apiModel = gson.fromJson(jsonString, AlphaVantageTimeSeriesResponse::class.java)

        // Expected percentage change: ((last.close - first.open) / first.open) * 100
        // first is 09:00:00 (open = 1.2350), last is 10:00:00 (close = 1.2450)
        // ((1.2450 - 1.2350) / 1.2350) * 100 = 0.8097... -> 0.81
        val expectedPercentage = Percentage("0.81")

        val expectedChart = ForexPairChart(
            currency = Currency.USD,
            priceHistory = persistentListOf(
                OhlcEntry(BigDecimal("1.2350"), BigDecimal("1.2450"), BigDecimal("1.2300"), BigDecimal("1.2400"), 1700038800L),
                OhlcEntry(BigDecimal("1.2450"), BigDecimal("1.2500"), BigDecimal("1.2400"), BigDecimal("1.2450"), 1700042400L)
            ),
            periodPriceChangePercentage = expectedPercentage
        )

        // Act
        val chart = forexChartMapper.mapTimeSeriesToChart(apiModel, Currency.USD)

        // Assert
        assertThat(chart.currency).isEqualTo(expectedChart.currency)
        assertThat(chart.periodPriceChangePercentage.value).isEqualTo(expectedChart.periodPriceChangePercentage.value)
        assertThat(chart.priceHistory).isEqualTo(expectedChart.priceHistory)
    }

    @Test
    fun `When OHLC data has invalid amount strings should filter out invalid values`() {
        // Arrange
        val jsonString = """
            {
              "Time Series FX (60min)": {
                "2023-11-15 10:00:00": {
                  "1. open": "abc",
                  "2. high": "1.2500",
                  "3. low": "1.2400",
                  "4. close": "1.2450"
                },
                "2023-11-15 09:00:00": {
                  "1. open": "1.2350",
                  "2. high": "1.2450",
                  "3. low": "1.2300",
                  "4. close": "1.2400"
                }
              }
            }
        """.trimIndent()
        val apiModel = gson.fromJson(jsonString, AlphaVantageTimeSeriesResponse::class.java)

        // Act
        val chart = forexChartMapper.mapTimeSeriesToChart(apiModel, Currency.USD)

        // Assert - The 10:00 entry should be filtered out due to invalid "open" string
        assertThat(chart.priceHistory.size).isEqualTo(1)
        assertThat(chart.priceHistory[0].open).isEqualTo(BigDecimal("1.2350"))
    }

    @Test
    fun `When currency is provided should preserve currency in chart`() {
        // Arrange
        val jsonString = """
            {
              "Time Series FX (60min)": {
                "2023-11-15 09:00:00": {
                  "1. open": "1.2350",
                  "2. high": "1.2450",
                  "3. low": "1.2300",
                  "4. close": "1.2400"
                }
              }
            }
        """.trimIndent()
        val apiModel = gson.fromJson(jsonString, AlphaVantageTimeSeriesResponse::class.java)

        // Act
        val gbpChart = forexChartMapper.mapTimeSeriesToChart(apiModel, Currency.GBP)
        val eurChart = forexChartMapper.mapTimeSeriesToChart(apiModel, Currency.EUR)

        // Assert
        assertThat(gbpChart.currency).isEqualTo(Currency.GBP)
        assertThat(eurChart.currency).isEqualTo(Currency.EUR)
    }
}
