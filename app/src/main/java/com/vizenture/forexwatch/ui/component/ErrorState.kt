package com.vizenture.forexwatch.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.vizenture.forexwatch.R

@Composable
fun ErrorState(
    message: String?,
    modifier: Modifier = Modifier
) {
    EmptyState(
        image = painterResource(R.drawable.error_state),
        title = stringResource(R.string.error_occurred),
        subtitle = {
            message?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier
    )
}
