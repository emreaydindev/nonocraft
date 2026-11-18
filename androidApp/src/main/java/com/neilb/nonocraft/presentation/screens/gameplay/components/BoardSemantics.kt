package com.neilb.nonocraft.presentation.screens.gameplay.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neilb.nonocraft.domain.model.Clue
import com.neilb.nonocraft.domain.model.Puzzle
import kotlin.math.roundToInt

/** Fill, cross and clear one cell without drawing (TalkBack custom actions), built on the view model's moves. */
interface BoardCellActions {
    fun fill(row: Int, col: Int, colorIndex: Int)
    fun cross(row: Int, col: Int)
    fun clear(row: Int, col: Int)
}

/**
 * Invisible TalkBack nodes over the drawn board: one per clue line ("Satır 3: 1, 1, 1") and one per cell
 * ("Satır 3, sütun 2" with its state). A double tap uses the current tool like a touch; the custom actions
 * fill with any palette colour, cross an empty cell or clear a cross. They take no touches, so drawing on
 * the board is unchanged. TalkBack reads them top to bottom: column clues, then each row's clue and cells.
 */
@Composable
internal fun BoardSemantics(
    puzzle: Puzzle,
    userGrid: List<List<Int>>,
    rowClues: List<List<Clue>>,
    colClues: List<List<Clue>>,
    maxRowClueCount: Int,
    maxColClueCount: Int,
    cellSize: Float,
    startX: Float,
    startY: Float,
    onCellClick: (row: Int, col: Int) -> Unit,
    cellActions: BoardCellActions?,
) {
    if (cellSize <= 0f) return
    val density = LocalDensity.current
    val cellDp = with(density) { cellSize.toDp() }
    val palette = puzzle.paletteHex

    fun Modifier.at(x: Float, y: Float) = offset { IntOffset(x.roundToInt(), y.roundToInt()) }

    colClues.forEachIndexed { col, clues ->
        val label = BoardSpeech.clue(isRow = false, index = col, clues = clues, palette = palette)
        Box(
            Modifier
                .at(startX + col * cellSize, startY - maxColClueCount * cellSize)
                .size(cellDp, cellDp * maxColClueCount)
                .semantics { contentDescription = label }
        )
    }

    userGrid.forEachIndexed { row, values ->
        val clueLabel = BoardSpeech.clue(isRow = true, index = row, clues = rowClues.getOrElse(row) { emptyList() }, palette = palette)
        Box(
            Modifier
                .at(startX - maxRowClueCount * cellSize, startY + row * cellSize)
                .size(cellDp * maxRowClueCount, cellDp)
                .semantics { contentDescription = clueLabel }
        )
        values.forEachIndexed { col, value ->
            val position = BoardSpeech.cellPosition(row, col)
            val state = BoardSpeech.cellState(value, palette)
            Box(
                Modifier
                    .at(startX + col * cellSize, startY + row * cellSize)
                    .size(cellDp.coerceAtLeast(1.dp))
                    .semantics {
                        contentDescription = position
                        stateDescription = state
                        // Filled cells are final; empty and crossed ones take moves.
                        if (value <= 0) {
                            onClick(label = "Seçili araçla işaretle") { onCellClick(row, col); true }
                            if (cellActions != null) customActions = cellActionsFor(row, col, value, palette, cellActions)
                        }
                    }
            )
        }
    }
}

private fun cellActionsFor(
    row: Int,
    col: Int,
    value: Int,
    palette: List<String>,
    actions: BoardCellActions,
): List<CustomAccessibilityAction> = buildList {
    val colorCount = palette.size.coerceAtLeast(1)
    for (colorIndex in 1..colorCount) {
        val label = if (colorCount > 1) "Doldur: ${BoardSpeech.colorLabel(colorIndex, palette)}" else "Doldur"
        add(CustomAccessibilityAction(label) { actions.fill(row, col, colorIndex); true })
    }
    if (value == 0) add(CustomAccessibilityAction("Çarpı koy") { actions.cross(row, col); true })
    if (value < 0) add(CustomAccessibilityAction("Temizle") { actions.clear(row, col); true })
}
