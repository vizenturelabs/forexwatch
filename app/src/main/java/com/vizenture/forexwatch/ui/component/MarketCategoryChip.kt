package com.vizenture.forexwatch.ui.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vizenture.forexwatch.data.source.local.datastore.common.MarketCategory

@Composable
fun MarketCategoryChip(
    category: MarketCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = category.displayName) },
        modifier = modifier
    )
}
