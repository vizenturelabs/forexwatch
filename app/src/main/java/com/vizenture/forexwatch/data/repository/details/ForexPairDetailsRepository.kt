package com.vizenture.forexwatch.data.repository.details

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.model.ForexPairDetails
import kotlinx.coroutines.flow.Flow

interface ForexPairDetailsRepository {
    fun getForexPairDetails(symbol: String, currency: Currency): Flow<Result<ForexPairDetails>>
}
