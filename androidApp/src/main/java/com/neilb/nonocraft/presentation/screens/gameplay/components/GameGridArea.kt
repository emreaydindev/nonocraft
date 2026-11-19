package com.neilb.nonocraft.presentation.screens.gameplay.components

import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.Density
import kotlin.math.floor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.domain.model.Clue
import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextPrimary
import kotlin.math.min

/**
 * The board with its clues, scaled to whatever space it gets: it rescales on a resize, fold/unfold or any
 * configuration change, and its zoom survives those (state is saveable; the game itself lives in the
 * shared view model). Touching, clicking or dragging over cells reports each cell once through
 * [onCellClick]; boards above 10×10 zoom with a pinch, Ctrl + mouse wheel or the wheel alone.
 */
@Composable
fun GameGridArea(
    puzzle: Puzzle,
    userGrid: List<List<Int>>,
    rowClues: List<List<Clue>>,
    colClues: List<List<Clue>>,
    cursorRow: Int,
    cursorCol: Int,
    onCellClick: (row: Int, col: Int) -> Unit,
    isCursorVisible: Boolean,
    modifier: Modifier = Modifier,
    cellActions: BoardCellActions? = null,
    showColorPatterns: Boolean = false,
) {
    val textMeasurer = rememberTextMeasurer()
    val isZoomEnabled = puzzle.size > 10
    val defaultScale = if (isZoomEnabled) 1.5f else 1f

    // Keyed by the board size so a new puzzle starts at the default zoom, but a recreation keeps it.
    var scale by rememberSaveable(puzzle.size) { mutableFloatStateOf(defaultScale) }
    var offsetX by rememberSaveable(puzzle.size) { mutableFloatStateOf(0f) }
    var offsetY by rememberSaveable(puzzle.size) { mutableFloatStateOf(0f) }
    val currentOnCellClick by rememberUpdatedState(onCellClick)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val availableWidth = with(density) { maxWidth.toPx() }
        val availableHeight = with(density) { maxHeight.toPx() }

        val maxRowClueCount = remember(rowClues) { rowClues.maxOfOrNull { it.size } ?: 1 }
        val maxColClueCount = remember(colClues) { colClues.maxOfOrNull { it.size } ?: 1 }
        val subGridSize = remember(puzzle.size) { getSubGridSize(puzzle.size) }

        val cellSize = min(availableWidth / (maxRowClueCount + puzzle.size), availableHeight / (maxColClueCount + puzzle.size))
        val startX = (availableWidth - ((maxRowClueCount + puzzle.size) * cellSize)) / 2f + (maxRowClueCount * cellSize)
        val startY = (availableHeight - ((maxColClueCount + puzzle.size) * cellSize)) / 2f + (maxColClueCount * cellSize)

        fun clampOffset() {
            val maxX = (availableWidth * (scale - 1)) / 2
            val maxY = (availableHeight * (scale - 1)) / 2
            offsetX = offsetX.coerceIn(-maxX, maxX)
            offsetY = offsetY.coerceIn(-maxY, maxY)
        }

        // A smaller window (fold, split screen) shrinks the pannable range; keep the board in view.
        LaunchedEffect(availableWidth, availableHeight) { clampOffset() }

        // Clue numbers are drawn with the font scale ignored: they stay a fixed share of the cell and
        // grow with the board, never with the system font size (which would overflow the cells).
        val clueDensity = remember(density.density) { Density(density.density, fontScale = 1f) }

        // The zoom applies to the drawing and to the TalkBack nodes over it alike.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale, scaleY = scale,
                    translationX = offsetX, translationY = offsetY
                )
        ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(puzzle.size, startX, startY, cellSize) {
                    // Like the iOS board: a touch or click registers on press, and a drag reports each
                    // cell once as the pointer enters it. Positions here are already in the layer's
                    // untransformed space.
                    awaitEachGesture {
                        var lastCell: Pair<Int, Int>? = null
                        fun report(position: Offset) {
                            val col = floor((position.x - startX) / cellSize).toInt()
                            val row = floor((position.y - startY) / cellSize).toInt()
                            if (row !in 0 until puzzle.size || col !in 0 until puzzle.size) return
                            if (lastCell == row to col) return
                            lastCell = row to col
                            currentOnCellClick(row, col)
                        }

                        val down = awaitFirstDown(requireUnconsumed = false)
                        // Right and middle mouse clicks don't paint (some input sources report no
                        // button at all, so only those are rejected rather than requiring primary).
                        val buttons = currentEvent.buttons
                        if (down.type == PointerType.Mouse && (buttons.isSecondaryPressed || buttons.isTertiaryPressed)) {
                            return@awaitEachGesture
                        }
                        report(down.position)
                        while (true) {
                            val event = awaitPointerEvent()
                            // A second finger means a pinch, not painting.
                            if (event.changes.count { it.pressed } > 1) break
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            report(change.position)
                        }
                    }
                }
                .pointerInput(isZoomEnabled, availableWidth, availableHeight) {
                    if (isZoomEnabled) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 3f)
                            offsetX += pan.x
                            offsetY += pan.y
                            clampOffset()
                        }
                    }
                }
                .pointerInput(isZoomEnabled, availableWidth, availableHeight) {
                    // Mouse wheel / trackpad scroll zooms big boards (Chromebooks, tablets with a mouse).
                    if (!isZoomEnabled) return@pointerInput
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type != PointerEventType.Scroll) continue
                            val delta = event.changes.firstOrNull()?.scrollDelta?.y ?: continue
                            if (delta == 0f) continue
                            scale = (scale * if (delta < 0) 1.1f else 1f / 1.1f).coerceIn(1f, 3f)
                            clampOffset()
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
        ) {
            drawRect(SurfaceDark, Offset(startX, startY), Size(puzzle.size * cellSize, puzzle.size * cellSize))

            // Shapes only tell colours apart, so a one-colour puzzle needs none.
            val patterns = showColorPatterns && puzzle.paletteHex.size > 1
            drawUserCells(userGrid, puzzle.paletteHex, cellSize, startX, startY, patterns, textMeasurer, clueDensity)
            drawGridLines(puzzle.size, subGridSize, cellSize, startX, startY)

            drawClues(rowClues, colClues, puzzle.paletteHex, maxRowClueCount, maxColClueCount, cellSize, startX, startY, textMeasurer, clueDensity, patterns)

            drawCursor(cursorRow, cursorCol, cellSize, startX, startY, isCursorVisible)
        }

        BoardSemantics(
            puzzle = puzzle,
            userGrid = userGrid,
            rowClues = rowClues,
            colClues = colClues,
            maxRowClueCount = maxRowClueCount,
            maxColClueCount = maxColClueCount,
            cellSize = cellSize,
            startX = startX,
            startY = startY,
            onCellClick = onCellClick,
            cellActions = cellActions,
        )
        }

        if (isZoomEnabled && (scale != defaultScale || offsetX != 0f || offsetY != 0f)) {
            IconButton(
                onClick = { scale = defaultScale; offsetX = 0f; offsetY = 0f },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(48.dp).background(SurfaceDark.copy(0.8f), CircleShape)
            ) { Icon(Icons.Rounded.ZoomOutMap, contentDescription = stringResource(R.string.game_reset_zoom), tint = NeonCyan) }
        }
    }
}

private fun DrawScope.drawClues(
    rowClues: List<List<Clue>>,
    colClues: List<List<Clue>>,
    paletteHex: List<String>,
    maxRowClueCount: Int,
    maxColClueCount: Int,
    cellSize: Float,
    startX: Float,
    startY: Float,
    textMeasurer: TextMeasurer,
    clueDensity: Density,
    showPatterns: Boolean,
) {
    // With colour patterns on, each clue number moves up and its colour's shape sits under it.
    fun drawClue(clue: Clue, x: Float, y: Float) {
        val color = paletteHex.getOrNull(clue.colorIndex - 1)?.toColor() ?: NeonCyan
        val textLayoutResult = textMeasurer.measure(
            text = clue.count.toString(),
            style = TextStyle(
                color = color,
                fontSize = with(clueDensity) { (cellSize * if (showPatterns) 0.38f else 0.45f).toSp() },
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont
            ),
            density = clueDensity
        )
        val textTop = if (showPatterns) y + cellSize * 0.04f else y + (cellSize - textLayoutResult.size.height) / 2f
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(x + (cellSize - textLayoutResult.size.width) / 2f, textTop)
        )
        if (showPatterns) {
            val glyph = cellSize * 0.24f
            drawPaletteGlyph(
                clue.colorIndex,
                Rect(Offset(x + (cellSize - glyph) / 2f, y + cellSize * 0.68f), Size(glyph, glyph)),
                color, textMeasurer, clueDensity
            )
        }
    }

    val clueBgColor = SurfaceDark.copy(alpha = 0.5f)

    drawRect(
        color = clueBgColor,
        topLeft = Offset(startX - (maxRowClueCount * cellSize), startY),
        size = Size(maxRowClueCount * cellSize, rowClues.size * cellSize)
    )

    drawRect(
        color = clueBgColor,
        topLeft = Offset(startX, startY - (maxColClueCount * cellSize)),
        size = Size(colClues.size * cellSize, maxColClueCount * cellSize)
    )

    rowClues.forEachIndexed { rowIndex, clues ->
        val y = startY + (rowIndex * cellSize)
        val missingClues = maxRowClueCount - clues.size

        clues.forEachIndexed { clueIndex, clue ->
            if (clue.count == 0) return@forEachIndexed

            val x = startX - ((maxRowClueCount - (missingClues + clueIndex)) * cellSize)
            drawClue(clue, x, y)
        }
    }

    colClues.forEachIndexed { colIndex, clues ->
        val x = startX + (colIndex * cellSize)
        val missingClues = maxColClueCount - clues.size

        clues.forEachIndexed { clueIndex, clue ->
            if (clue.count == 0) return@forEachIndexed

            val y = startY - ((maxColClueCount - (missingClues + clueIndex)) * cellSize)
            drawClue(clue, x, y)
        }
    }
}

private fun DrawScope.drawUserCells(
    userGrid: List<List<Int>>,
    paletteHex: List<String>,
    cellSize: Float,
    startX: Float,
    startY: Float,
    showPatterns: Boolean,
    textMeasurer: TextMeasurer,
    textDensity: Density,
) {
    for (row in userGrid.indices) {
        for (col in userGrid[row].indices) {
            val cellValue = userGrid[row][col]
            val x = startX + (col * cellSize)
            val y = startY + (row * cellSize)

            if (cellValue > 0) {
                val color = paletteHex.getOrNull(cellValue - 1)?.toColor() ?: NeonCyan
                drawRect(color = color, topLeft = Offset(x, y), size = Size(cellSize, cellSize))
                if (showPatterns) {
                    val inset = cellSize * 0.3f
                    drawPaletteGlyph(
                        cellValue,
                        Rect(Offset(x + inset, y + inset), Size(cellSize - 2 * inset, cellSize - 2 * inset)),
                        PaletteGlyph.contrastOn(color), textMeasurer, textDensity
                    )
                }
            } else if (cellValue == -1) {
                val padding = cellSize * 0.25f
                val path = Path().apply {
                    moveTo(x + padding, y + padding)
                    lineTo(x + cellSize - padding, y + cellSize - padding)
                    moveTo(x + cellSize - padding, y + padding)
                    lineTo(x + padding, y + cellSize - padding)
                }
                drawPath(path = path, color = DangerRed, style = Stroke(width = cellSize * 0.1f))
            }
        }
    }
}

private fun DrawScope.drawGridLines(size: Int, subGridSize: Int, cellSize: Float, startX: Float, startY: Float) {
    val thinStroke = 1.dp.toPx()
    val thickStroke = 3.dp.toPx()
    val gridPixelWidth = size * cellSize
    val gridPixelHeight = size * cellSize

    for (i in 0..size) {
        val isThick = i % subGridSize == 0
        val strokeWidth = if (isThick) thickStroke else thinStroke
        val color = if (isThick) TextPrimary.copy(alpha = 0.5f) else BorderDark

        drawLine(color = color, start = Offset(startX + (i * cellSize), startY), end = Offset(startX + (i * cellSize), startY + gridPixelHeight), strokeWidth = strokeWidth)
        drawLine(color = color, start = Offset(startX, startY + (i * cellSize)), end = Offset(startX + gridPixelWidth, startY + (i * cellSize)), strokeWidth = strokeWidth)
    }
}

private fun DrawScope.drawCursor(cursorRow: Int, cursorCol: Int, cellSize: Float, startX: Float, startY: Float, isCursorVisible: Boolean) {
    if (!isCursorVisible) return
    val x = startX + (cursorCol * cellSize)
    val y = startY + (cursorRow * cellSize)

    drawRect(color = NeonCyan, topLeft = Offset(x, y), size = Size(cellSize, cellSize), style = Stroke(width = 3.dp.toPx()))
    drawRect(color = NeonCyan.copy(alpha = 0.2f), topLeft = Offset(x, y), size = Size(cellSize, cellSize))
}

private fun getSubGridSize(size: Int): Int = when (size) {
    6, 9 -> 3
    8, 12 -> 4
    else -> 5
}