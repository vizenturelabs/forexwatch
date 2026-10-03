package dev.shorthouse.coinwatch.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.shorthouse.coinwatch.R
import dev.shorthouse.coinwatch.data.source.local.datastore.common.PairSort
import androidx.compose.ui.tooling.preview.PreviewWrapper
import dev.shorthouse.coinwatch.ui.preview.AppPreviewWrapper

@Composable
fun CoinSortChip(
    coinSort: PairSort,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        label = { Text(text = stringResource(coinSort.labelId)) },
        onClick = onClick,
        selected = selected,
        leadingIcon = {
            Icon(
                imageVector = coinSort.imageVector,
                tint = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                contentDescription = null
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.surface,
            selectedLabelColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = MaterialTheme.shapes.small,
        border = null,
        modifier = modifier
    )
}

private val PairSort.labelId: Int
    get() = when (this) {
        PairSort.MarketCap -> R.string.market_coin_sort_market_cap
        PairSort.Popular -> R.string.market_coin_sort_popular
        PairSort.Gainers -> R.string.market_coin_sort_gainers
        PairSort.Losers -> R.string.market_coin_sort_losers
        PairSort.Newest -> R.string.market_coin_sort_newest
    }

private val PairSort.imageVector: ImageVector
    get() = when (this) {
        PairSort.MarketCap -> Icons.Rounded.BarChart
        PairSort.Popular -> Icons.Rounded.Whatshot
        PairSort.Gainers -> Icons.AutoMirrored.Rounded.TrendingUp
        PairSort.Losers -> Icons.AutoMirrored.Rounded.TrendingDown
        PairSort.Newest -> Icons.Rounded.MoreTime
    }

@Preview(showBackground = true)
@PreviewWrapper(wrapper = AppPreviewWrapper::class)
@Composable
private fun CoinSortChipUnselectedPreview() {
    CoinSortChip(
        coinSort = PairSort.MarketCap,
        selected = false,
        onClick = {}
    )

}

@Preview(showBackground = true)
@PreviewWrapper(wrapper = AppPreviewWrapper::class)
@Composable
private fun CoinSortChipSelectedPreview() {
    CoinSortChip(
        coinSort = PairSort.Gainers,
        selected = true,
        onClick = {}
    )

}
