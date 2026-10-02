package com.vizenture.forexwatch.data.repository.details

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.mapper.ForexChartMapper
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.model.ForexPairDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ForexPairDetailsRepositoryImpl @Inject constructor(
    private val forexNetworkDataSource: ForexNetworkDataSource,
    private val forexChartMapper: ForexChartMapper,
) : ForexPairDetailsRepository {

    override fun getForexPairDetails(symbol: String, currency: Currency): Flow<Result<ForexPairDetails>> {
        return flow {
            emit(Result.Loading)
            try {
                val fromSymbol = symbol.substring(0, 3)
                val toSymbol = symbol.substring(3, 6)

                val response = forexNetworkDataSource.getExchangeRate(fromSymbol, toSymbol)
                val details = forexChartMapper.mapExchangeRateToDetails(response, currency)

                if (details != null) {
                    emit(Result.Success(details))
                } else {
                    emit(Result.Error("Failed to parse details"))
                }
            } catch (e: Exception) {
                emit(Result.Error("Failed to fetch details"))
            }
        }
    }
}
