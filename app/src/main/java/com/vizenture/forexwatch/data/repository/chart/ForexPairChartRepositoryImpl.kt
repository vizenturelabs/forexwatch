package com.vizenture.forexwatch.data.repository.chart

import com.vizenture.forexwatch.common.Constants
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.mapper.ForexChartMapper
import com.vizenture.forexwatch.data.source.local.database.dao.CachedChartDao
import com.vizenture.forexwatch.data.source.local.database.model.CachedChart
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.domain.manager.TerminalManager
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.ui.model.ChartPeriod
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class ForexPairChartRepositoryImpl @Inject constructor(
    private val forexNetworkDataSource: ForexNetworkDataSource,
    private val forexChartMapper: ForexChartMapper,
    private val cachedChartDao: CachedChartDao,
    private val terminalManager: TerminalManager
) : ForexPairChartRepository {

    override fun getForexPairChart(
        symbol: String,
        chartPeriod: ChartPeriod,
        currency: Currency,
        refreshIntervalMillis: Long
    ): Flow<Result<ForexPairChart>> = flow {
        emit(Result.Loading)

        val terminalId = terminalManager.getActiveTerminalId().toInt()
        val periodString = chartPeriod.stringName

        // 1. CHECK LOCAL CACHE
        val cached = cachedChartDao.getCachedChart(symbol, periodString, terminalId)
        val isCacheFresh = cached != null && (System.currentTimeMillis() - cached.lastUpdated) < refreshIntervalMillis

        if (isCacheFresh) {
            emit(Result.Success(mapCachedToChart(cached, currency)))
            return@flow
        }

        // 2. FETCH FROM API (Cache is stale or missing)
        try {
            val fromSymbol = symbol.substring(0, 3)
            val toSymbol = symbol.substring(3, 6)
            val tdSymbol = Constants.getStandardForexSymbol(fromSymbol, toSymbol)

            val interval = when (chartPeriod) {
                ChartPeriod.Hour -> "5min"
                ChartPeriod.Day -> "1h"
                ChartPeriod.Week, ChartPeriod.Month, ChartPeriod.ThreeMonth -> "1day"
                ChartPeriod.Year -> "1week"
                ChartPeriod.FiveYear -> "1month"
            }

            val response = forexNetworkDataSource.getTimeSeries(tdSymbol, interval)
            val chart = forexChartMapper.mapTimeSeriesToChart(response, currency)

            // 3. SAVE TO CACHE
            cachedChartDao.insertCachedChart(
                CachedChart(
                    symbol = symbol,
                    period = periodString,
                    terminalId = terminalId,
                    data = chart.priceHistory,
                    lastUpdated = System.currentTimeMillis()
                )
            )

            emit(Result.Success(chart))
        } catch (e: Exception) {
            // 4. FALLBACK: If API fails, show stale cache instead of error!
            if (cached != null) {
                emit(Result.Success(mapCachedToChart(cached, currency)))
            } else {
                emit(Result.Error("Failed to fetch chart data: ${e.message}"))
            }
        }
    }
        // OPTIMIZATION: Simplified boolean expression to prevent identical cache data from triggering redundant UI updates
        .distinctUntilChanged { oldResult, newResult ->
            oldResult is Result.Success &&
                newResult is Result.Success &&
                oldResult.data.priceHistory == newResult.data.priceHistory
        }

    private fun mapCachedToChart(cached: CachedChart, currency: Currency): ForexPairChart {
        val first = cached.data.first().open
        val last = cached.data.last().close
        val change = if (first != BigDecimal.ZERO) ((last - first) / first) * BigDecimal(100) else BigDecimal.ZERO

        return ForexPairChart(
            currency = currency,
            priceHistory = cached.data.toPersistentList(),
            periodPriceChangePercentage = Percentage(change.setScale(2, RoundingMode.HALF_EVEN).toString())
        )
    }
}
