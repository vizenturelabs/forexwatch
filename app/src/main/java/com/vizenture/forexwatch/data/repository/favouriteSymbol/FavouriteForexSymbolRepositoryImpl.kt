package com.vizenture.forexwatch.data.repository.favouriteSymbol

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.ForexLocalDataSource
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FavouriteForexSymbolRepositoryImpl @Inject constructor(
    private val forexLocalDataSource: ForexLocalDataSource
) : FavouriteForexSymbolRepository {

    override fun isForexPairFavourite(symbol: String, terminalId: Int): Flow<Result<Boolean>> {
        return flow {
            emit(Result.Loading)
            try {
                forexLocalDataSource.isForexPairFavourite(symbol, terminalId).collect { isFavourite ->
                    emit(Result.Success(isFavourite))
                }
            } catch (e: Exception) {
                emit(Result.Error("Failed to check favourite status"))
            }
        }
    }

    override suspend fun toggleIsForexPairFavourite(favouriteSymbol: FavouriteSymbol): Boolean {
        return try {
            val isCurrentlyFavourite = forexLocalDataSource.isForexPairFavouriteOneShot(
                favouriteSymbol.symbol,
                favouriteSymbol.terminalId
            )

            if (isCurrentlyFavourite) {
                forexLocalDataSource.deleteFavouriteSymbol(favouriteSymbol)
                false
            } else {
                forexLocalDataSource.insertFavouriteSymbol(favouriteSymbol)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
