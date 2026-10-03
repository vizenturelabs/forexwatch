package com.vizenture.forexwatch.ui.screen.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.common.Result
import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import com.vizenture.forexwatch.data.source.local.datastore.global.Currency
import com.vizenture.forexwatch.domain.manager.TerminalManager
import com.vizenture.forexwatch.domain.usecase.market.GetForexPairsUseCase
import com.vizenture.forexwatch.ui.model.TimeOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val getForexPairsUseCase: GetForexPairsUseCase,
    private val terminalManager: TerminalManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MarketUiState(
            pairs = persistentListOf(),
            pairSort = PairSort.Volume,
            timeOfDay = TimeOfDay.Morning, // Default value for now
            marketChangePercentage24h = null,
            currency = Currency.USD,
            isRefreshing = false,
            errorMessageIds = emptyList(),
            isLoading = true
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        initialiseUiState()
    }

    private fun initialiseUiState() {
        val terminalId = terminalManager.getActiveTerminalId().toInt()
        val currentSort = _uiState.value.pairSort
        val currentCurrency = _uiState.value.currency

        getForexPairsUseCase(
            terminalId = terminalId,
            pairSort = currentSort,
            currency = currentCurrency
        ).onEach { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            pairs = result.data.toPersistentList(),
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            errorMessageIds = listOf(R.string.error_network_coins),
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)

        refreshPairs()
    }

    fun refreshPairs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val terminalId = terminalManager.getActiveTerminalId().toInt()
            getForexPairsUseCase.refresh(terminalId, _uiState.value.pairSort, _uiState.value.currency)
        }
    }

    fun updatePairSort(pairSort: PairSort) {
        _uiState.update { it.copy(pairSort = pairSort) }
        refreshPairs()
    }

    fun dismissErrorMessage(errorMessageId: Int) {
        _uiState.update {
            it.copy(errorMessageIds = it.errorMessageIds.filter { id -> id != errorMessageId })
        }
    }
}
