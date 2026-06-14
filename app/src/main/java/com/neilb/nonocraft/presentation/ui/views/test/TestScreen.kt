package com.neilb.nonocraft.presentation.ui.views.test

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neilb.nonocraft.R
import com.neilb.nonocraft.domain.model.Block
import com.neilb.nonocraft.domain.model.Game
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.test_page))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = null)
                    }
                },
            )
        }
    ) { paddingValues ->
        TestScreenContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun TestScreenContent(
    modifier: Modifier
) {
    val solved = listOf(
        "___XX___X_",
        "__XXXX_XX_",
        "__XXXX_XXX",
        "_XXXXX_XXX",
        "_XXXXX_XX_",
        "__XXX__X__",
        "___X____X_",
        "__X____XX_",
        "_X_____X__",
        "_______X__"
    ).map { rowString ->
        rowString.map { char ->
            if (char == 'X') Block(Block.black, Color.Black) else Block(Block.empty)
        }
    }

    val testGame = Game(
        id = UUID.randomUUID().toString(),
        size = 10,
        type = 0,
        solvedTable = solved,
        numberOfLives = 5
    )

    NonogramBoard(game = testGame, modifier = modifier)
}

@Composable
fun NonogramBoard(
    game: Game,
    modifier: Modifier = Modifier
) {
    val size = game.size
    val hintSpaceRatio = 0.4f // İpucu alanının toplam alana oranı

    var userTable by remember {
        mutableStateOf(List(size) { List(size) { Block(Block.notSelected) } })
    }

    var dragMode by remember { mutableStateOf<Int?>(null) }
    var lastDraggedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }


    val rowHints by remember {
        derivedStateOf {
            game.solvedTable.map { row ->
                val hints = row.joinToString("") { if (it.status == Block.black) "B" else "E" }
                    .split("E")
                    .filter { it.isNotEmpty() }
                    .map { it.length }
                if (hints.isEmpty()) listOf(0) else hints
            }
        }
    }

    val colHints by remember {
        derivedStateOf {
            (0 until size).map { colIndex ->
                val colString = (0 until size).map { rowIndex ->
                    if (game.solvedTable[rowIndex][colIndex].status == Block.black) "B" else "E"
                }.joinToString("")
                val hints = colString.split("E")
                    .filter { it.isNotEmpty() }
                    .map { it.length }
                if (hints.isEmpty()) listOf(0) else hints
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // En büyük kare alanı hesapla
        val totalSize = min(maxWidth, maxHeight)
        val hintSize = 100.dp
        val gameGridSize = totalSize - hintSize
        val cellSize = gameGridSize / size

        Column {
            // Üst Sütun İpuçları
            Row {
                Box(modifier = Modifier.width(hintSize)) // Sol üst köşe boşluğu
                Row(modifier = Modifier.width(gameGridSize)) {
                    colHints.forEach { hints ->
                        Column(
                            modifier = Modifier
                                .width(cellSize)
                                .height(hintSize)
                                .padding(bottom = 4.dp)
                                .border(1.dp, Color.DarkGray),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            hints.filter { it > 0 }.forEach { hint ->
                                Text(
                                    text = hint.toString(),
                                    fontSize = (cellSize.value / 2.8).sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = (cellSize.value / 2.5).sp
                                )
                            }
                        }
                    }
                }
            }
            // Satır İpuçları ve Oyun Tahtası
            Row {
                // Sol Satır İpuçları
                Column(modifier = Modifier.width(hintSize)) {
                    rowHints.forEach { hints ->
                        Row(
                            modifier = Modifier
                                .height(cellSize)
                                .fillMaxWidth()
                                .padding(end = 4.dp)
                                .border(1.dp, Color.DarkGray),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            hints.filter { it > 0 }.forEach { hint ->
                                Text(
                                    text = hint.toString(),
                                    fontSize = (cellSize.value / 2.8).sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = (cellSize.value / 12).dp)
                                )
                            }
                        }
                    }
                }

                // Oyun Tahtası (Grid)
                val density = LocalDensity.current
                Column(
                    modifier = Modifier
                        .width(gameGridSize)
                        .height(gameGridSize)
                        .border(2.dp, Color.DarkGray)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val col = (offset.x / with(density) { cellSize.toPx() }).toInt().coerceIn(0, size - 1)
                                    val row = (offset.y / with(density) { cellSize.toPx() }).toInt().coerceIn(0, size - 1)

                                    val currentStatus = userTable[row][col].status
                                    dragMode = when (currentStatus) {
                                        Block.notSelected -> Block.black
                                        Block.black -> Block.empty
                                        else -> Block.notSelected
                                    }

                                    userTable = userTable.toMutableList().also {
                                        it[row] = it[row].toMutableList().also { r ->
                                            r[col] = Block(dragMode!!)
                                        }
                                    }
                                    lastDraggedCell = Pair(row, col)
                                },
                                onDragEnd = { dragMode = null; lastDraggedCell = null },
                                onDragCancel = { dragMode = null; lastDraggedCell = null }
                            ) { change, _ ->
                                change.consume()
                                val col = (change.position.x / with(density) { cellSize.toPx() }).toInt().coerceIn(0, size - 1)
                                val row = (change.position.y / with(density) { cellSize.toPx() }).toInt().coerceIn(0, size - 1)

                                if (dragMode != null && Pair(row, col) != lastDraggedCell) {
                                    userTable = userTable.toMutableList().also {
                                        it[row] = it[row].toMutableList().also { r ->
                                            r[col] = Block(dragMode!!)
                                        }
                                    }
                                    lastDraggedCell = Pair(row, col)
                                }
                            }
                        }
                ) {
                    for (r in 0 until size) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (c in 0 until size) {
                                val currentBlock = userTable[r][c]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(0.5.dp, Color.LightGray)
                                        .background(
                                            when (currentBlock.status) {
                                                Block.black -> Color.Black
                                                else -> Color.White
                                            }
                                        )
                                ) {
                                    if (currentBlock.status == Block.empty) {
                                        Text(
                                            text = "X",
                                            color = Color.Gray,
                                            fontSize = (cellSize.value / 2).sp,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}