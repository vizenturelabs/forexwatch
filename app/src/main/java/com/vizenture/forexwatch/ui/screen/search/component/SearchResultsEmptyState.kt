package com.vizenture.forexwatch.ui.screen.search.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.ui.component.EmptyState

@Composable
fun SearchResultsEmptyState(modifier: Modifier = Modifier) {
    EmptyState(
        image = painterResource(R.drawable.empty_state_search_query),
        title = stringResource(R.string.empty_state_search_results_title),
        subtitle = {
            Text(
                text = stringResource(R.string.empty_state_search_results_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier.padding(bottom = 12.dp)
    )
}
