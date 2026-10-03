package com.vizenture.forexwatch.data.repository.searchResults

import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.mapper.ForexSearchResultsMapper // Ensure this exists and is updated
import com.vizenture.forexwatch.data.source.remote.ForexNetworkDataSource
import com.vizenture.forexwatch.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ForexSearchResultsRepositoryImpl @Inject constructor(
    private val forexNetworkDataSource: ForexNetworkDataSource,
    private val forexSearchResultsMapper: ForexSearchResultsMapper
) : ForexSearchResultsRepository {

    override fun getSearchResults(query: String): Flow<Result<List<SearchResult>>> = flow {
        emit(Result.Loading)
        try {
            val response = forexNetworkDataSource.searchSymbols(query)
            val results = forexSearchResultsMapper.mapToDomain(response)
            emit(Result.Success(results))
        } catch (e: Exception) {
            emit(Result.Error(e.message))
        }
    }
}
