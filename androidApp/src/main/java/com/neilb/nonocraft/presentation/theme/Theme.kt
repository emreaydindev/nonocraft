package com.neilb.nonocraft.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NonocraftColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = PurpleAccent,
    tertiary = GameGold,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    error = DangerRed,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    outlineVariant = TextMuted
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