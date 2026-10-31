package com.vizenture.forexwatch.domain.usecase.search

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.repository.searchResults.ForexSearchResultsRepository
import com.vizenture.forexwatch.model.SearchResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForexSearchResultsUseCase @Inject constructor(
    private val repository: ForexSearchResultsRepository
) {
    operator fun invoke(query: String): Flow<Result<List<SearchResult>>> {
        return repository.getSearchResults(query)
    }
}
