package com.vizenture.forexwatch.ui.screen.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.data.source.local.database.model.CurrencyPair
import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort
import com.vizenture.forexwatch.ui.component.LoadingIndicator
import com.vizenture.forexwatch.ui.component.PairSortChip
import com.vizenture.forexwatch.ui.component.ScrollToTopFab
import com.vizenture.forexwatch.ui.insets.AppWindowInsets
import com.vizenture.forexwatch.ui.model.TimeOfDay
import com.vizenture.forexwatch.ui.screen.market.component.MarketDropdownMenu
import com.vizenture.forexwatch.ui.screen.market.component.MarketPairItem
import com.vizenture.forexwatch.ui.screen.market.component.SearchPrompt
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
fun MarketScreen(
    onNavigateDetails: (String) -> Unit,
    onNavigateSettings: () -> Unit,
    viewModel: MarketViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MarketScreen(
        uiState = uiState,
        onPairClick = { pair ->
            onNavigateDetails(pair.symbol)
        },
        onNavigateSettings = onNavigateSettings,
        onUpdatePairSort = { pairSort ->
            viewModel.updatePairSort(pairSort)
        },
        onRefresh = {
            viewModel.refreshPairs()
        },
        onDismissError = { errorMessageId ->
            viewModel.dismissErrorMessage(errorMessageId)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    uiState: MarketUiState,
    onPairClick: (CurrencyPair) -> Unit,
    onNavigateSettings: () -> Unit,
    onUpdatePairSort: (PairSort) -> Unit,
    onRefresh: () -> Unit,
    onDismissError: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()
    val showScrollToTopFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 1
        }
    }

    Scaffold(
        topBar = {
            MarketTopBar(
                timeOfDay = uiState.timeOfDay,
                onNavigateSettings = onNavigateSettings,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToTopFab,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                ScrollToTopFab(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                )
            }
        },
        contentWindowInsets = AppWindowInsets.horizontalContent,
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { scaffoldPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            state = pullRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = uiState.isRefreshing,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    state = pullRefreshState
                )
            },
            modifier = Modifier
                .padding(scaffoldPadding)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }

                else -> {
                    MarketContent(
                        pairs = uiState.pairs,
                        onPairClick = onPairClick,
                        pairSort = uiState.pairSort,
                        onUpdatePairSort = onUpdatePairSort,
                        onRefresh = onRefresh,
                        lazyListState = listState
                    )
                }
            }
            if (uiState.errorMessageIds.isNotEmpty()) {
                val errorMessage = stringResource(uiState.errorMessageIds.first())

                LaunchedEffect(errorMessage, snackbarHostState) {
                    snackbarHostState.showSnackbar(message = errorMessage)
                    onDismissError(uiState.errorMessageIds.first())
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MarketTopBar(
    timeOfDay: TimeOfDay,
    onNavigateSettings: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column(modifier = Modifier.animateContentSize()) {
                Text(
                    text = stringResource(R.string.time_of_day_prefix_good) +
                        " " + timeOfDay.name.lowercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = stringResource(R.string.cd_more),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            MarketDropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                onNavigateSettings = onNavigateSettings
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
private fun MarketContent(
    pairs: ImmutableList<CurrencyPair>,
    onPairClick: (CurrencyPair) -> Unit,
    pairSort: PairSort,
    onUpdatePairSort: (PairSort) -> Unit,
    onRefresh: () -> Unit,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    if (pairs.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.market_empty_state_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.market_empty_state_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRefresh) {
                Text(stringResource(R.string.action_retry))
            }
        }
    } else {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
            modifier = modifier
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp)
                ) {
                    PairSort.entries.forEach { pairSortEntry ->
                        PairSortChip(
                            pairSort = pairSortEntry,
                            selected = pairSortEntry == pairSort,
                            onClick = { onUpdatePairSort(pairSortEntry) }
                        )
                    }
                }
            }
            items(
                count = pairs.size,
                key = { pairs[it].symbol },
                itemContent = { index ->
                    val pairListItem = pairs[index]

                    val cardShape = when (index) {
                        0 -> MaterialTheme.shapes.medium.copy(
                            bottomStart = CornerSize(0.dp),
                            bottomEnd = CornerSize(0.dp)
                        )

                        pairs.lastIndex -> MaterialTheme.shapes.medium.copy(
                            topStart = CornerSize(0.dp),
                            topEnd = CornerSize(0.dp)
                        )

                        else -> RoundedCornerShape(0.dp)
                    }

                    MarketPairItem(
                        pair = pairListItem,
                        onPairClick = { onPairClick(pairListItem) },
                        cardShape = cardShape
                    )
                }
            )

            item {
                SearchPrompt(modifier = Modifier.padding(vertical = 24.dp))
            }
        }
    }
}
