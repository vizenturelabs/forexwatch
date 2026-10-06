package com.vizenture.forexwatch.domain.preferences

import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import com.vizenture.forexwatch.data.source.local.datastore.market.MarketPreferencesRepository
import javax.inject.Inject

class UpdateMarketCoinSortUseCase @Inject constructor(
    private val marketPreferencesRepository: MarketPreferencesRepository,
) {
    suspend operator fun invoke(coinSort: PairSort) {
        updateMarketCoinSort(coinSort = coinSort)
    }

    private suspend fun updateMarketCoinSort(coinSort: PairSort) {
        marketPreferencesRepository.updateCoinSort(coinSort = coinSort)
    }
}
