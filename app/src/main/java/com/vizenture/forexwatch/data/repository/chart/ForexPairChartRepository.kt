package com.vizenture.forexwatch.data.repository.chart

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.ui.model.ChartPeriod
import kotlinx.coroutines.flow.Flow

interface ForexPairChartRepository {
    fun getForexPairChart(symbol: String, chartPeriod: ChartPeriod, currency: Currency): Flow<Result<ForexPairChart>>
}
