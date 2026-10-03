package com.vizenture.forexwatch.data.mapper

import com.vizenture.forexwatch.data.source.remote.model.AlphaVantageSearchResponse
import com.vizenture.forexwatch.model.SearchResult
import javax.inject.Inject

class ForexSearchResultsMapper @Inject constructor() {

    fun mapToDomain(apiModel: AlphaVantageSearchResponse): List<SearchResult> {
        return apiModel.bestMatches?.map { match ->
            SearchResult(
                symbol = match.symbol,
                name = match.name,
                type = match.type
            )
        } ?: emptyList()
    }
}
