package com.vizenture.forexwatch.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vizenture.forexwatch.data.source.local.datastore.common.PairSort

@Composable
fun RowScope.PairSortChip(
    pairSort: PairSort,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = pairSort.displayName) },
        modifier = modifier
    )
}
