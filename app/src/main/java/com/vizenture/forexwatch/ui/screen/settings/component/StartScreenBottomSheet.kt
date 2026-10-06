package com.vizenture.forexwatch.ui.screen.settings.component

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.vizenture.forexwatch.R
import com.vizenture.forexwatch.data.source.local.datastore.global.StartScreen
import com.vizenture.forexwatch.ui.component.AppBottomSheet
import com.vizenture.forexwatch.ui.component.BottomSheetOption
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreenBottomSheet(
    sheetState: SheetState,
    selectedStartScreen: StartScreen,
    onStartScreenSelected: (StartScreen) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val startScreenOptions = remember {
        persistentListOf(
            StartScreenOption(
                icon = Icons.Rounded.BarChart,
                labelId = StartScreen.Market.nameId,
                startScreen = StartScreen.Market
            ),
            StartScreenOption(
                icon = Icons.Rounded.Favorite,
                labelId = StartScreen.Favourites.nameId,
                startScreen = StartScreen.Favourites
            ),
            StartScreenOption(
                icon = Icons.Rounded.WifiTethering,
                labelId = StartScreen.Pulse.nameId,
                startScreen = StartScreen.Pulse
            ),
            StartScreenOption(
                icon = Icons.Rounded.Search,
                labelId = StartScreen.Search.nameId,
                startScreen = StartScreen.Search
            )
        )
    }

    AppBottomSheet(
        title = stringResource(R.string.start_screen_bottom_sheet_title),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier
    ) {
        startScreenOptions.forEach { option ->
            BottomSheetOption(
                icon = option.icon,
                label = stringResource(option.labelId),
                isSelected = option.startScreen == selectedStartScreen,
                onSelected = { onStartScreenSelected(option.startScreen) }
            )
        }
    }
}

private data class StartScreenOption(
    val icon: ImageVector,
    @StringRes val labelId: Int,
    val startScreen: StartScreen,
)
