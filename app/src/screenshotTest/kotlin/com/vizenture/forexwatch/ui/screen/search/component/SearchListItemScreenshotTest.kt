package dev.shorthouse.coinwatch.ui.screen.search.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.android.tools.screenshot.PreviewTest
import dev.shorthouse.coinwatch.model.SearchResult
import dev.shorthouse.coinwatch.ui.preview.SearchCoinPreviewProvider
import dev.shorthouse.coinwatch.ui.theme.AppTheme

@PreviewTest
@Preview
@Composable
fun SearchListItemScreenshotTest(
    @PreviewParameter(SearchCoinPreviewProvider::class) searchResult: SearchResult,
) {
    AppTheme {
        SearchListItem(
            searchResult = searchResult,
            onCoinClick = {},
            cardShape = MaterialTheme.shapes.medium
        )
    }
}
