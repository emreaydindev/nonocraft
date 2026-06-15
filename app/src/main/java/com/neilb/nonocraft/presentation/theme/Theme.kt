package com.neilb.nonocraft.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NonocraftColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = BackgroundDark,

    secondary = NeonPink,
    onSecondary = TextWhite,

    tertiary = ElectricPurple,
    onTertiary = TextWhite,

    background = BackgroundDark,
    onBackground = TextWhite,

    surface = SurfaceDark,
    onSurface = TextWhite,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextWhite,

    error = GameOrange,
    onError = TextWhite
)

@Composable
fun NonogramTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NonocraftColorScheme,
        typography = Typography,
        content = content
    )
}