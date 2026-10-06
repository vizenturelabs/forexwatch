package com.vizenture.forexwatch.data.repository.favouritePair

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.mapper.ForexChartMapper
import com.vizenture.forexwatch.data.source.local.database.ForexLocalDataSource
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FavouriteForexPairRepositoryImpl @Inject constructor(
    private val forexNetworkDataSource: ForexNetworkDataSource,
    private val forexLocalDataSource: ForexLocalDataSource,
    private val forexChartMapper: ForexChartMapper,
) : FavouriteForexPairRepository {

    override fun getFavouriteForexPairs(terminalId: Int): Flow<Result<List<FavouriteCurrencyPair>>> {
        return flow {
            emit(Result.Loading)
            try {
                // Observe local database first
                forexLocalDataSource.getFavouriteForexPairs(terminalId).collect { localPairs ->
                    emit(Result.Success(localPairs))
                }
            } catch (e: Exception) {
                emit(Result.Error("Failed to load favourite pairs"))
            }
        }
    }

    // Optional: Add a refresh function here if you want pull-to-refresh on the favourites screen
}
