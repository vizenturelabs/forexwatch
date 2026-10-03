package com.vizenture.forexwatch.data.repository.pair

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import kotlinx.coroutines.flow.Flow

interface ForexPairRepository {
    suspend fun getRemoteForexPairs(terminalId: Int, pairSort: PairSort, currency: Currency): Result<List<CurrencyPair>>
    fun getCachedForexPairs(terminalId: Int): Flow<Result<List<CurrencyPair>>>
    suspend fun updateCachedForexPairs(terminalId: Int, pairs: List<CurrencyPair>)
}
