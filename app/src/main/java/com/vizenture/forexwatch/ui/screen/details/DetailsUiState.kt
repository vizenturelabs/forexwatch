package com.vizenture.forexwatch.ui.screen.details

import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.ForexPairDetails
import com.vizenture.forexwatch.ui.model.ChartPeriod

sealed interface DetailsUiState {

    data class Success(
        val pairDetails: ForexPairDetails,
        val pairChart: ForexPairChart,
        val chartPeriod: ChartPeriod,
        val isPairFavourite: Boolean
    ) : DetailsUiState

    data class Error(val message: String?) : DetailsUiState

    data object Loading : DetailsUiState
}
