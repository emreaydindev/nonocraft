package com.neilb.nonocraft.presentation.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun GridBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color = NeonCyan,
    alpha: Float = 0.06f,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val step = 72f
        val color = primaryColor.copy(alpha = alpha)
        var x = 0f
        while (x <= size.width) {
            drawLine(color, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += step
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += step
        }
    }
}

