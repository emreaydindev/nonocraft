package com.neilb.nonocraft.presentation.screens.gameplay.components

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.toColorInt
import com.neilb.nonocraft.R
import com.neilb.nonocraft.domain.model.Clue

/**
 * What TalkBack says about the board: cell positions and states, clues as one phrase per line and palette
 * colours by name, in the app language. iOS builds the same phrases from the same keys in `BoardSpeech.swift`.
 */
class BoardSpeech(private val res: Resources) {

    fun cellPosition(row: Int, col: Int): String = res.getString(R.string.board_cell_position, row + 1, col + 1)

    /** 0 empty, -1 crossed, > 0 a palette colour (named only when the puzzle has more than one). */
    fun cellState(value: Int, palette: List<String>): String = when {
        value == 0 -> res.getString(R.string.board_empty)
        value < 0 -> res.getString(R.string.board_crossed)
        palette.size > 1 -> res.getString(R.string.board_filled_color, colorLabel(value, palette))
        else -> res.getString(R.string.board_filled)
    }

    /** "Row 3: 1, 1, 1"; in colour puzzles "Row 3: 2 red, 1 blue"; "Row 3: empty" for an empty line. */
    fun clue(isRow: Boolean, index: Int, clues: List<Clue>, palette: List<String>): String {
        val parts = clues.filter { it.count > 0 }.map { clue ->
            if (palette.size > 1) {
                res.getString(R.string.board_clue_part, clue.count, colorName(palette.getOrNull(clue.colorIndex - 1)))
            } else {
                "${clue.count}"
            }
        }
        val text = if (parts.isEmpty()) res.getString(R.string.board_empty) else parts.joinToString(", ")
        return res.getString(if (isRow) R.string.board_clue_row else R.string.board_clue_column, index + 1, text)
    }

    /** "colour 2, blue" for palette index 2 (1-based, as in the grid). */
    fun colorLabel(colorIndex: Int, palette: List<String>): String =
        res.getString(R.string.board_color_label, colorIndex, colorName(palette.getOrNull(colorIndex - 1)))

    fun colorName(hex: String?): String = res.getString(colorNameRes(hex))

    companion object {
        /** A plain colour word for a hex colour, from its hue, saturation and brightness. */
        @StringRes
        fun colorNameRes(hex: String?): Int {
            val argb = runCatching { hex?.toColorInt() }.getOrNull() ?: return R.string.color_unknown
            val hsv = FloatArray(3)
            android.graphics.Color.colorToHSV(argb, hsv)
            val (hue, saturation, value) = Triple(hsv[0], hsv[1], hsv[2])
            return when {
                value < 0.2f -> R.string.color_black
                saturation < 0.15f -> if (value > 0.85f) R.string.color_white else R.string.color_grey
                hue < 15f || hue >= 345f -> R.string.color_red
                hue < 40f -> if (value < 0.6f) R.string.color_brown else R.string.color_orange
                hue < 65f -> R.string.color_yellow
                hue < 160f -> R.string.color_green
                hue < 195f -> R.string.color_turquoise
                hue < 250f -> R.string.color_blue
                hue < 290f -> R.string.color_purple
                else -> R.string.color_pink
            }
        }
    }
}

/** Board speech in the current app language; rebuilt when the configuration (locale) changes. */
@Composable
fun rememberBoardSpeech(): BoardSpeech {
    val configuration = LocalConfiguration.current
    val resources = LocalContext.current.resources
    return remember(configuration, resources) { BoardSpeech(resources) }
}
