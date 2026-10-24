package com.vizenture.forexwatch.ui.screen.favourites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.ViewStream
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.data.source.local.database.model.FavouriteCurrencyPair
import com.vizenture.forexwatch.ui.component.LoadingIndicator
import com.vizenture.forexwatch.ui.component.ScrollToTopFab
import com.vizenture.forexwatch.ui.insets.AppWindowInsets
import com.vizenture.forexwatch.ui.screen.favourites.component.FavouriteCondensedItem
import com.vizenture.forexwatch.ui.screen.favourites.component.FavouritePairItem
import com.vizenture.forexwatch.ui.screen.favourites.component.FavouritesEmptyState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@Composable
fun FavouritesScreen(
    viewModel: FavouritesViewModel = hiltViewModel(),
    onNavigateDetails: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavouriteScreen(
        uiState = uiState,
        onPairClick = { pair -> onNavigateDetails(pair.symbol) },
        onUpdateIsFavouritesCondensed = { isCondensed -> viewModel.updateIsFavouritesCondensed(isCondensed) },
        onRefresh = { viewModel.pullRefreshFavouritePairs() },
        onDismissError = { errorMessageId -> viewModel.dismissErrorMessage(errorMessageId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    uiState: FavouritesUiState,
    onPairClick: (FavouriteCurrencyPair) -> Unit,
    onUpdateIsFavouritesCondensed: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onDismissError: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val gridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    val showScrollToTopFab by remember { derivedStateOf { gridState.firstVisibleItemIndex > 0 } }

    Scaffold(
        topBar = {
            FavouritesTopBar(
                isFavouritesCondensed = uiState.isFavouritesCondensed,
                onUpdateIsFavouritesCondensed = onUpdateIsFavouritesCondensed,
                isFavouritesListEmpty = uiState.isFavouritePairsEmpty,
                isLoading = uiState.isLoading,
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
                    onClick = { scope.launch { gridState.animateScrollToItem(0) } }
                )
            }
        },
        contentWindowInsets = AppWindowInsets.horizontalContent,
        modifier = modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)
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
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            when {
                uiState.isLoading -> LoadingIndicator()
                else -> {
                    FavouritesContent(
                        favouritePairs = uiState.favouritePairs,
                        onPairClick = onPairClick,
                        isFavouritesCondensed = uiState.isFavouritesCondensed,
                        gridState = gridState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesTopBar(
    isFavouritesCondensed: Boolean,
    onUpdateIsFavouritesCondensed: (Boolean) -> Unit,
    isFavouritesListEmpty: Boolean,
    isLoading: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.favourites_screen),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        actions = {
            if (!isLoading && !isFavouritesListEmpty) {
                IconButton(onClick = { onUpdateIsFavouritesCondensed(!isFavouritesCondensed) }) {
                    Icon(
                        imageVector = if (isFavouritesCondensed) Icons.Rounded.GridView else Icons.Rounded.ViewStream,
                        contentDescription = if (isFavouritesCondensed) stringResource(R.string.cd_expand_favourites) else stringResource(R.string.cd_condense_favourites),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
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
fun FavouritesContent(
    favouritePairs: ImmutableList<FavouriteCurrencyPair>,
    onPairClick: (FavouriteCurrencyPair) -> Unit,
    isFavouritesCondensed: Boolean,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        when {
            favouritePairs.isEmpty() -> FavouritesEmptyState()
            else -> FavouritesGrid(favouritePairs, onPairClick, isFavouritesCondensed, gridState, modifier)
        }
    }
}

@Composable
fun FavouritesGrid(
    favouritePairs: ImmutableList<FavouriteCurrencyPair>,
    onPairClick: (FavouriteCurrencyPair) -> Unit,
    isFavouritesCondensed: Boolean,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        state = gridState,
        columns = if (isFavouritesCondensed) GridCells.Fixed(1) else GridCells.Adaptive(minSize = 160.dp),
        horizontalArrangement = Arrangement.spacedBy(if (isFavouritesCondensed) 0.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(if (isFavouritesCondensed) 0.dp else 16.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 80.dp),
        modifier = modifier,
    ) {
        items(
            count = favouritePairs.size,
            key = { favouritePairs[it].symbol },
            itemContent = { index ->
                val pair = favouritePairs[index]
                if (isFavouritesCondensed) {
                    val cardShape = when {
                        favouritePairs.size == 1 -> MaterialTheme.shapes.medium
                        index == 0 -> MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
                        index == favouritePairs.lastIndex -> MaterialTheme.shapes.medium.copy(topStart = CornerSize(0.dp), topEnd = CornerSize(0.dp))
                        else -> RoundedCornerShape(0.dp)
                    }
                    FavouriteCondensedItem(
                        favouritePair = pair,
                        onNavigateDetails = { onPairClick(pair) },
                        cardShape = cardShape,
                        modifier = Modifier.animateItem(placementSpec = tween(200, easing = FastOutSlowInEasing))
                    )
                } else {
                    FavouritePairItem(
                        favouritePair = pair,
                        onNavigateDetails = { onPairClick(pair) },
                        modifier = Modifier.animateItem(placementSpec = tween(200, easing = FastOutSlowInEasing))
                    )
                }
            }
        )
    }
}

