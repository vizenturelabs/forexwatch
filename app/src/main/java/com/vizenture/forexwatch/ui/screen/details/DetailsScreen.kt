package com.vizenture.forexwatch.ui.screen.details

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.model.ForexPairChart
import com.vizenture.forexwatch.model.ForexPairDetails
import com.vizenture.forexwatch.ui.component.ErrorState
import com.vizenture.forexwatch.ui.component.LoadingIndicator
import com.vizenture.forexwatch.ui.insets.AppWindowInsets
import com.vizenture.forexwatch.ui.model.ChartPeriod
import com.vizenture.forexwatch.ui.screen.details.component.EmptyTopBar
import com.vizenture.forexwatch.ui.screen.details.component.ForexPairChartCard

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailsScreen(
        uiState = uiState,
        onNavigateUp = onNavigateUp,
        onClickFavouritePair = { viewModel.toggleIsPairFavourite() },
        onClickChartPeriod = { chartPeriod ->
            viewModel.updateChartPeriod(chartPeriod)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    uiState: DetailsUiState,
    onNavigateUp: () -> Unit,
    onClickFavouritePair: () -> Unit,
    onClickChartPeriod: (ChartPeriod) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            when (uiState) {
                is DetailsUiState.Success -> {
                    DetailsTopBar(
                        pairDetails = uiState.pairDetails,
                        isPairFavourite = uiState.isPairFavourite,
                        onNavigateUp = onNavigateUp,
                        onClickFavouritePair = onClickFavouritePair,
                        scrollBehavior = scrollBehavior
                    )
                }

                else -> {
                    EmptyTopBar(
                        onNavigateUp = onNavigateUp
                    )
                }
            }
        },
        contentWindowInsets = AppWindowInsets.horizontalAndBottomContent,
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { scaffoldPadding ->
        when (uiState) {
            is DetailsUiState.Success -> {
                DetailsContent(
                    pairDetails = uiState.pairDetails,
                    pairChart = uiState.pairChart,
                    chartPeriod = uiState.chartPeriod,
                    onClickChartPeriod = onClickChartPeriod,
                    scrollState = scrollState,
                    modifier = Modifier.padding(scaffoldPadding)
                )
            }

            is DetailsUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    modifier = Modifier.padding(scaffoldPadding)
                )
            }

            is DetailsUiState.Loading -> {
                LoadingIndicator(modifier = Modifier.padding(scaffoldPadding))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DetailsTopBar(
    pairDetails: ForexPairDetails,
    isPairFavourite: Boolean,
    onNavigateUp: () -> Unit,
    onClickFavouritePair: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.cd_top_bar_back)
                )
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pairDetails.symbol,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = pairDetails.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pairDetails.symbol.take(3),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onClickFavouritePair) {
                Icon(
                    imageVector = if (isPairFavourite) {
                        Icons.Rounded.Favorite
                    } else {
                        Icons.Rounded.FavoriteBorder
                    },
                    contentDescription = stringResource(R.string.cd_top_bar_favourite),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun DetailsContent(
    pairDetails: ForexPairDetails,
    pairChart: ForexPairChart,
    chartPeriod: ChartPeriod,
    onClickChartPeriod: (ChartPeriod) -> Unit,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
    ) {
        ForexPairChartCard(
            currentPrice = pairDetails.currentPrice,
            pairChart = pairChart,
            chartPeriod = chartPeriod,
            onClickChartPeriod = onClickChartPeriod
        )

        // ForexMarketStatsCard removed as it is not applicable for Forex pairs
    }
}

