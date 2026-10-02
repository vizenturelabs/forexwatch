package com.vizenture.forexwatch.data.repository.chart

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.mapper.ForexChartMapper
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.ui.model.ChartPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ForexPairChartRepositoryImpl @Inject constructor(
    private val forexNetworkDataSource: ForexNetworkDataSource,
    private val forexChartMapper: ForexChartMapper,
) : ForexPairChartRepository {

    override fun getForexPairChart(
        symbol: String,
        chartPeriod: ChartPeriod,
        currency: Currency
    ): Flow<Result<ForexPairChart>> {
        return flow {
            emit(Result.Loading)
            try {
                val fromSymbol = symbol.substring(0, 3)
                val toSymbol = symbol.substring(3, 6)

                val response = when (chartPeriod) {
                    ChartPeriod.Hour, ChartPeriod.Day -> {
                        forexNetworkDataSource.getForexIntraday(fromSymbol, toSymbol, "60min")
                    }
                    else -> {
                        forexNetworkDataSource.getForexDaily(fromSymbol, toSymbol)
                    }
                }

                val chart = forexChartMapper.mapTimeSeriesToChart(response, currency)
                emit(Result.Success(chart))
            } catch (e: Exception) {
                emit(Result.Error("Failed to fetch chart data"))
            }
        }
    }
}
