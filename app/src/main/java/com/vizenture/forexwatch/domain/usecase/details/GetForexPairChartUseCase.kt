package com.vizenture.forexwatch.domain.usecase.details

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.repository.chart.ForexPairChartRepository
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.ui.model.ChartPeriod
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForexPairChartUseCase @Inject constructor(
    private val repository: ForexPairChartRepository
) {
    // FIX: Added refreshIntervalMillis parameter
    operator fun invoke(
        symbol: String,
        chartPeriod: ChartPeriod,
        currency: Currency,
        refreshIntervalMillis: Long
    ): Flow<Result<ForexPairChart>> {
        return repository.getForexPairChart(symbol, chartPeriod, currency, refreshIntervalMillis)
    }
}
