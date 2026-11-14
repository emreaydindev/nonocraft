package com.neilb.nonocraft.presentation.components

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.scrollbar(
    scrollState: ScrollState,
    width: Dp = 4.dp,
    thumbColor: Color,
    trackColor: Color = Color.White.copy(alpha = 0.05f),
    cornerRadius: Float = 8f,
    endPadding: Float = 16f
): Modifier = drawWithContent {
    drawContent()

    if (scrollState.maxValue > 0) {
        val viewportHeight = size.height
        val totalContentHeight = scrollState.maxValue.toFloat() + viewportHeight
        val scrollValue = scrollState.value.toFloat()

        val scrollBarHeight = (viewportHeight / totalContentHeight) * viewportHeight
        val scrollBarStartOffset = (scrollValue / totalContentHeight) * viewportHeight

        drawRoundRect(
            cornerRadius = CornerRadius(cornerRadius),
            color = trackColor,
            topLeft = Offset(size.width - endPadding, 0f),
            size = Size(width.toPx(), viewportHeight),
        )

        drawRoundRect(
            cornerRadius = CornerRadius(cornerRadius),
            color = thumbColor,
            topLeft = Offset(size.width - endPadding, scrollBarStartOffset),
            size = Size(width.toPx(), scrollBarHeight)
        )
    }
}