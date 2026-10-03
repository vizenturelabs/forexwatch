package com.vizenture.forexwatch.domain.usecase.details

import com.vizenture.forexwatch.data.repository.details.ForexPairDetailsRepository
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.model.ForexPairDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForexPairDetailsUseCase @Inject constructor(
    private val repository: ForexPairDetailsRepository
) {
    operator fun invoke(symbol: String, currency: Currency): Flow<Result<ForexPairDetails>> {
        return repository.getForexPairDetails(symbol, currency)
    }
}
