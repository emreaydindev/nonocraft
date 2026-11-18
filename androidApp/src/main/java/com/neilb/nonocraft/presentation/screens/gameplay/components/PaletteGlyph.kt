package com.neilb.nonocraft.presentation.screens.gameplay.components

import android.content.SharedPreferences
import android.provider.Settings as SystemSettings
import androidx.core.content.edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density

/**
 * Colour-blind support: every palette colour also has its own shape, drawn on filled cells, next to colour
 * clues and on the palette swatches, so colours can be told apart without seeing them. iOS draws the same
 * shapes in `PaletteGlyph.swift`. Colours past the tenth show their number instead.
 */
object PaletteGlyph {
    const val SHAPE_COUNT = 10

    /** Black or white, whichever reads better on [fill]. */
    fun contrastOn(fill: Color): Color = if (fill.luminance() > 0.45f) Color.Black.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.85f)
}

/** Draws palette colour [colorIndex]'s shape (1-based) centred in [bounds]. */
fun DrawScope.drawPaletteGlyph(
    colorIndex: Int,
    bounds: Rect,
    color: Color,
    textMeasurer: TextMeasurer? = null,
    textDensity: Density? = null,
) {
    val side = minOf(bounds.width, bounds.height)
    val box = Rect(Offset(bounds.center.x - side / 2, bounds.center.y - side / 2), Size(side, side))
    fun p(x: Float, y: Float) = Offset(box.left + x * side, box.top + y * side)
    val stroke = Stroke(width = side * 0.16f)
    fun polygon(vararg xy: Float) = Path().apply {
        val points = xy.toList().chunked(2) { p(it[0], it[1]) }
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    when (colorIndex) {
        1 -> drawCircle(color, radius = side * 0.42f, center = box.center)
        2 -> drawPath(polygon(0.5f, 0.06f, 0.95f, 0.9f, 0.05f, 0.9f), color)
        3 -> drawRect(color, p(0.12f, 0.12f), Size(side * 0.76f, side * 0.76f))
        4 -> drawPath(polygon(0.5f, 0f, 1f, 0.5f, 0.5f, 1f, 0f, 0.5f), color)
        5 -> {
            drawRect(color, p(0.36f, 0.04f), Size(side * 0.28f, side * 0.92f))
            drawRect(color, p(0.04f, 0.36f), Size(side * 0.92f, side * 0.28f))
        }
        6 -> drawCircle(color, radius = side * 0.36f, center = box.center, style = stroke)
        7 -> drawRect(color, p(0.02f, 0.34f), Size(side * 0.96f, side * 0.32f))
        8 -> drawRect(color, p(0.34f, 0.02f), Size(side * 0.32f, side * 0.96f))
        9 -> drawPath(polygon(0.05f, 0.1f, 0.95f, 0.1f, 0.5f, 0.94f), color)
        10 -> drawRect(color, p(0.16f, 0.16f), Size(side * 0.68f, side * 0.68f), style = stroke)
        else -> if (textMeasurer != null && textDensity != null) {
            val layout = textMeasurer.measure(
                text = colorIndex.toString(),
                style = TextStyle(color = color, fontSize = with(textDensity) { (side * 0.8f).toSp() }, fontWeight = FontWeight.Bold),
                density = textDensity
            )
            drawText(layout, topLeft = Offset(box.center.x - layout.size.width / 2f, box.center.y - layout.size.height / 2f))
        }
    }
}

/**
 * The "Renk körü desenleri" setting. Until the player picks, it follows the system colour correction
 * (Accessibility > Colour correction). Kept on this device only (platform preferences, not the shared store), like iOS keeps it in UserDefaults.
 */
class ColorPatternSetting(private val prefs: SharedPreferences, systemDefault: Boolean) {
    private val state: MutableState<Boolean> =
        mutableStateOf(if (prefs.contains(KEY)) prefs.getBoolean(KEY, false) else systemDefault)

    var isEnabled: Boolean
        get() = state.value
        set(value) {
            prefs.edit { putBoolean(KEY, value) }
            state.value = value
        }

    private companion object {
        const val KEY = "a11y_color_patterns"
    }
}

private const val PREFS = "accessibility"

@Composable
fun rememberColorPatternSetting(): ColorPatternSetting {
    val context = LocalContext.current
    return remember(context) {
        val isColorCorrectionOn = SystemSettings.Secure.getInt(
            context.contentResolver, "accessibility_display_daltonizer_enabled", 0
        ) == 1
        ColorPatternSetting(context.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE), isColorCorrectionOn)
    }
}
