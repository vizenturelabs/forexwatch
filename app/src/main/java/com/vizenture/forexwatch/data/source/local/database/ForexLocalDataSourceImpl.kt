package com.vizenture.forexwatch.data.source.local.database

import com.vizenture.forexwatch.data.source.local.database.dao.CurrencyPairDao
import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteCurrencyPairDao
import com.vizenture.forexwatch.data.source.local.database.dao.FavouriteSymbolDao
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteSymbol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForexLocalDataSourceImpl @Inject constructor(
    private val currencyPairDao: CurrencyPairDao,
    private val favouriteCurrencyPairDao: FavouriteCurrencyPairDao,
    private val favouriteSymbolDao: FavouriteSymbolDao
) : ForexLocalDataSource {

    override fun getForexPairs(terminalId: Int): Flow<List<CurrencyPair>> {
        return currencyPairDao.getForexPairs(terminalId)
    }

    override suspend fun updateForexPairs(terminalId: Int, pairs: List<CurrencyPair>) {
        currencyPairDao.updateForexPairs(terminalId, pairs)
    }

    override fun getFavouriteForexPairs(terminalId: Int): Flow<List<FavouriteCurrencyPair>> {
        return favouriteCurrencyPairDao.getFavouriteForexPairs(terminalId)
    }

    override suspend fun updateFavouriteForexPairs(terminalId: Int, pairs: List<FavouriteCurrencyPair>) {
        favouriteCurrencyPairDao.updateFavouriteForexPairs(terminalId, pairs)
    }

    // NEW: Implementation for bookmarked symbols
    override fun getFavouriteSymbols(terminalId: Int): Flow<List<FavouriteSymbol>> {
        return favouriteSymbolDao.getFavouriteSymbols(terminalId)
    }

    override fun isForexPairFavourite(symbol: String, terminalId: Int): Flow<Boolean> {
        return favouriteSymbolDao.isForexPairFavourite(symbol, terminalId)
    }

    override suspend fun isForexPairFavouriteOneShot(symbol: String, terminalId: Int): Boolean {
        return favouriteSymbolDao.isForexPairFavouriteOneShot(symbol, terminalId)
    }

    override suspend fun insertFavouriteSymbol(favouriteSymbol: FavouriteSymbol) {
        favouriteSymbolDao.insert(favouriteSymbol)
    }

    override suspend fun deleteFavouriteSymbol(favouriteSymbol: FavouriteSymbol) {
        favouriteSymbolDao.delete(favouriteSymbol)
    }
}
