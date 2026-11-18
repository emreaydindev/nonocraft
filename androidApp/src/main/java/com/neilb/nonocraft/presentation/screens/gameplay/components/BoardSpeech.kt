package com.neilb.nonocraft.presentation.screens.gameplay.components

import androidx.core.graphics.toColorInt
import com.neilb.nonocraft.domain.model.Clue

/**
 * What TalkBack says about the board: cell positions and states, clues as one phrase per line and palette
 * colours by name. Turkish like the rest of the UI until localization moves these to resources; iOS builds
 * the same phrases in `BoardSpeech.swift`.
 */
object BoardSpeech {

    fun cellPosition(row: Int, col: Int): String = "Satır ${row + 1}, sütun ${col + 1}"

    /** 0 empty, -1 crossed, > 0 a palette colour (named only when the puzzle has more than one). */
    fun cellState(value: Int, palette: List<String>): String = when {
        value == 0 -> "boş"
        value < 0 -> "çarpı"
        palette.size > 1 -> "dolu, ${colorLabel(value, palette)}"
        else -> "dolu"
    }

    /** "Satır 3: 1, 1, 1"; in colour puzzles "Satır 3: 2 kırmızı, 1 mavi"; "Satır 3: boş" for an empty line. */
    fun clue(isRow: Boolean, index: Int, clues: List<Clue>, palette: List<String>): String {
        val name = if (isRow) "Satır" else "Sütun"
        val parts = clues.filter { it.count > 0 }.map { clue ->
            if (palette.size > 1) "${clue.count} ${colorName(palette.getOrNull(clue.colorIndex - 1))}" else "${clue.count}"
        }
        return "$name ${index + 1}: ${if (parts.isEmpty()) "boş" else parts.joinToString(", ")}"
    }

    /** "renk 2, mavi" for palette index 2 (1-based, as in the grid). */
    fun colorLabel(colorIndex: Int, palette: List<String>): String =
        "renk $colorIndex, ${colorName(palette.getOrNull(colorIndex - 1))}"

    /** A plain colour word for a hex colour, from its hue, saturation and brightness. */
    fun colorName(hex: String?): String {
        val argb = runCatching { hex?.toColorInt() }.getOrNull() ?: return "renkli"
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(argb, hsv)
        val (hue, saturation, value) = Triple(hsv[0], hsv[1], hsv[2])
        return when {
            value < 0.2f -> "siyah"
            saturation < 0.15f -> if (value > 0.85f) "beyaz" else "gri"
            hue < 15f || hue >= 345f -> "kırmızı"
            hue < 40f -> if (value < 0.6f) "kahverengi" else "turuncu"
            hue < 65f -> "sarı"
            hue < 160f -> "yeşil"
            hue < 195f -> "turkuaz"
            hue < 250f -> "mavi"
            hue < 290f -> "mor"
            else -> "pembe"
        }
    }
}
