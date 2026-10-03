package com.vizenture.forexwatch.data.repository.searchResults

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface ForexSearchResultsRepository {
    fun getSearchResults(query: String): Flow<Result<List<SearchResult>>>
}
