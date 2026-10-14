package com.vizenture.forexwatch.data.repository.pair

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import kotlinx.coroutines.flow.Flow

interface ForexPairRepository {
    fun getCachedForexPairs(terminalId: Int): Flow<Result<List<CurrencyPair>>>
    suspend fun getRemoteForexPairs(terminalId: Int, category: MarketCategory, currency: Currency): Result<List<CurrencyPair>>
    suspend fun updateCachedForexPairs(terminalId: Int, pairs: List<CurrencyPair>)
}
