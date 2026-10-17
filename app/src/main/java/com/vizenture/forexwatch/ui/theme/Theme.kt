package com.vizenture.forexwatch.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val darkColorScheme = darkColorScheme(
    primary = dark_primary,
    primaryContainer = dark_primaryContainer,
    onPrimaryContainer = dark_onPrimaryContainer,
    background = dark_background,
    onBackground = dark_onBackground,
    surface = dark_surface,
    onSurface = dark_onSurface,
    onSurfaceVariant = dark_onSurfaceVariant
)

// Fallback light color scheme using default Material 3 colors
private val lightColorScheme = lightColorScheme()

@Composable
fun AppTheme(
    isDarkMode: Boolean = false,
    isDynamicColorEnabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        isDynamicColorEnabled && supportsDynamicColor && isDarkMode -> dynamicDarkColorScheme(context)
        isDynamicColorEnabled && supportsDynamicColor && !isDarkMode -> dynamicLightColorScheme(context)
        isDarkMode -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = AppShapes,
        typography = AppTypography,
        content = content
    )
}
