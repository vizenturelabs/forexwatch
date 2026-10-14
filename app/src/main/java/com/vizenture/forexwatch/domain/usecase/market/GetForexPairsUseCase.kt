package com.vizenture.forexwatch.domain.usecase.market

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.repository.pair.ForexPairRepository
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForexPairsUseCase @Inject constructor(
    private val repository: ForexPairRepository
) {
    operator fun invoke(
        terminalId: Int,
        category: MarketCategory,
        currency: Currency
    ): Flow<Result<List<CurrencyPair>>> {
        return repository.getCachedForexPairs(terminalId)
    }

    suspend fun refresh(terminalId: Int, category: MarketCategory, currency: Currency) {
        repository.getRemoteForexPairs(terminalId, category, currency)
    }
}
