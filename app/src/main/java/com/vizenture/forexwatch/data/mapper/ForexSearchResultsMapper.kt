package com.vizenture.forexwatch.data.mapper

import com.vizenture.forexwatch.data.source.remote.model.TwelveDataSearchResponse
import com.vizenture.forexwatch.model.SearchResult
import javax.inject.Inject

class ForexSearchResultsMapper @Inject constructor() {

    fun mapToDomain(apiModel: TwelveDataSearchResponse): List<SearchResult> {
        return apiModel.data?.mapNotNull { match ->
            // Twelve Data returns symbols like "EUR/USD", we need "EURUSD" for app consistency
            val cleanSymbol = match.symbol.replace("/", "")

            SearchResult(
                symbol = cleanSymbol,
                name = match.instrumentName,
                type = match.instrumentType ?: "Unknown"
            )
        } ?: emptyList()
    }
}
