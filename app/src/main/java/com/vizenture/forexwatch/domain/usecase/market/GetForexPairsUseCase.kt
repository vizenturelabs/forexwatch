package com.vizenture.forexwatch.domain.usecase.market

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.repository.pair.ForexPairRepository
import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForexPairsUseCase @Inject constructor(
    private val repository: ForexPairRepository
) {
    operator fun invoke(
        terminalId: Int,
        pairSort: PairSort,
        currency: Currency
    ): Flow<Result<List<CurrencyPair>>> {
        return repository.getCachedForexPairs(terminalId)
    }

    suspend fun refresh(terminalId: Int, pairSort: PairSort, currency: Currency) {
        repository.getRemoteForexPairs(terminalId, pairSort, currency)
    }
}
