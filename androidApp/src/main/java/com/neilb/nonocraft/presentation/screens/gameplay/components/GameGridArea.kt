package com.neilb.nonocraft.presentation.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val isZoomEnabled = puzzle.size > 10

    LaunchedEffect(puzzle.size) {
        scale = if (isZoomEnabled) 1.5f else 1f
        offset = Offset.Zero
    }

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

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale, scaleY = scale,
                    translationX = offset.x, translationY = offset.y
                )
                .pointerInput(scale, offset, startX, startY, cellSize) {
                    detectDragGestures(
                        onDragStart = { tapOffset ->
                            val transformedX = (tapOffset.x - offset.x) / scale
                            val transformedY = (tapOffset.y - offset.y) / scale
                            val col = ((transformedX - startX) / cellSize).toInt()
                            val row = ((transformedY - startY) / cellSize).toInt()
                            if (row in 0 until puzzle.size && col in 0 until puzzle.size) {
                                onCellClick(row, col)
                            }
                        },
                        onDrag = { change, _ ->
                            val transformedX = (change.position.x - offset.x) / scale
                            val transformedY = (change.position.y - offset.y) / scale
                            val col = ((transformedX - startX) / cellSize).toInt()
                            val row = ((transformedY - startY) / cellSize).toInt()
                            if (row in 0 until puzzle.size && col in 0 until puzzle.size) {
                                onCellClick(row, col)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    if (isZoomEnabled) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 3f)
                            val maxX = (availableWidth * (scale - 1)) / 2
                            val maxY = (availableHeight * (scale - 1)) / 2
                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )
                        }
                    }
                }
        ) {
            drawRect(SurfaceDark, Offset(startX, startY), Size(puzzle.size * cellSize, puzzle.size * cellSize))

            drawUserCells(userGrid, puzzle.paletteHex, cellSize, startX, startY)
            drawGridLines(puzzle.size, subGridSize, cellSize, startX, startY)

            drawClues(rowClues, colClues, puzzle.paletteHex, maxRowClueCount, maxColClueCount, cellSize, startX, startY, textMeasurer)

            drawCursor(cursorRow, cursorCol, cellSize, startX, startY, isCursorVisible)
        }

        if (isZoomEnabled && (scale > 1.0f || offset != Offset.Zero)) {
            IconButton(
                onClick = { scale = 1.5f; offset = Offset.Zero },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).size(48.dp).background(SurfaceDark.copy(0.8f), CircleShape)
            ) { Icon(Icons.Rounded.ZoomOutMap, null, tint = NeonCyan) }
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
    textMeasurer: TextMeasurer
) {
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
            val color = paletteHex.getOrNull(clue.colorIndex - 1)?.toColor() ?: NeonCyan

            val textLayoutResult = textMeasurer.measure(
                text = clue.count.toString(),
                style = TextStyle(color = color, fontSize = (cellSize * 0.45f).sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(x + (cellSize - textLayoutResult.size.width) / 2f, y + (cellSize - textLayoutResult.size.height) / 2f)
            )
        }
    }

    colClues.forEachIndexed { colIndex, clues ->
        val x = startX + (colIndex * cellSize)
        val missingClues = maxColClueCount - clues.size

        clues.forEachIndexed { clueIndex, clue ->
            if (clue.count == 0) return@forEachIndexed

            val y = startY - ((maxColClueCount - (missingClues + clueIndex)) * cellSize)
            val color = paletteHex.getOrNull(clue.colorIndex - 1)?.toColor() ?: NeonCyan

            val textLayoutResult = textMeasurer.measure(
                text = clue.count.toString(),
                style = TextStyle(color = color, fontSize = (cellSize * 0.45f).sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(x + (cellSize - textLayoutResult.size.width) / 2f, y + (cellSize - textLayoutResult.size.height) / 2f)
            )
        }
    }
}

private fun DrawScope.drawUserCells(userGrid: List<List<Int>>, paletteHex: List<String>, cellSize: Float, startX: Float, startY: Float) {
    for (row in userGrid.indices) {
        for (col in userGrid[row].indices) {
            val cellValue = userGrid[row][col]
            val x = startX + (col * cellSize)
            val y = startY + (row * cellSize)

            if (cellValue > 0) {
                val color = paletteHex.getOrNull(cellValue - 1)?.toColor() ?: NeonCyan
                drawRect(color = color, topLeft = Offset(x, y), size = Size(cellSize, cellSize))
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