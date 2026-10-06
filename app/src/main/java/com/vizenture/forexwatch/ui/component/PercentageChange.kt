package com.vizenture.forexwatch.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.vizenture.forexwatch.model.Percentage
import com.vizenture.forexwatch.ui.theme.NegativeRed
import com.vizenture.forexwatch.ui.theme.PositiveGreen

@Composable
fun PercentageChange(
    percentage: Percentage,
    modifier: Modifier = Modifier,
) {
    val textColor = when {
        percentage.isPositive -> PositiveGreen
        percentage.isNegative -> NegativeRed
        else -> MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = percentage.formattedAmount,
        style = MaterialTheme.typography.bodyMedium,
        color = textColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}


