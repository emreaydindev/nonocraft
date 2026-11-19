package com.neilb.nonocraft.presentation.screens.intro

import com.neilb.nonocraft.presentation.accessibility.rememberReduceMotion
import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.neilb.nonocraft.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

private enum class Tool { FILL, CROSS, PURPLE, GOLD }
private enum class CellState { EMPTY, CYAN, PURPLE, GOLD, CROSS }

@Composable
fun TutorialScreen(onNavigateToDashboard: () -> Unit) {
    val pagerState = rememberPagerState { 4 }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var selectedTool by remember { mutableStateOf(Tool.FILL) }

    // Grids for each step
    val grid1 = remember { mutableStateListOf(CellState.EMPTY, CellState.EMPTY, CellState.EMPTY, CellState.EMPTY) }
    val grid2 = remember { mutableStateListOf(CellState.CYAN, CellState.EMPTY, CellState.CYAN, CellState.CYAN, CellState.EMPTY) }
    val grid3 = remember { mutableStateListOf(CellState.EMPTY, CellState.EMPTY, CellState.EMPTY, CellState.EMPTY, CellState.EMPTY) }

    // Animation for hand
    val handX = remember { Animatable(0f) }
    val page = pagerState.currentPage
    val reduceMotion = rememberReduceMotion()

    LaunchedEffect(page, grid3.count { it != CellState.EMPTY }) {
        // Reduce Motion: the hand rests at the start of the stroke instead of sweeping over and over.
        if (reduceMotion) {
            handX.snapTo(if (page == 2) (grid3.count { it != CellState.EMPTY } * 150).toFloat() else 0f)
            return@LaunchedEffect
        }
        if (page == 0) {
            while (true) {
                handX.animateTo(150f, tween(1200, easing = LinearOutSlowInEasing))
                delay(400.milliseconds)
                handX.snapTo(0f)
            }
        } else if (page == 2) {
            val filledCount = grid3.count { it != CellState.EMPTY }
            val startX = (filledCount * 150).toFloat()
            val endX = startX + 150f
            
            handX.snapTo(startX)
            if (filledCount < 5) {
                while (true) {
                    handX.animateTo(endX, tween(800, easing = LinearOutSlowInEasing))
                    delay(300.milliseconds)
                    handX.snapTo(startX)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .safeContentPadding()
            .navigationBarsPadding()
    ) {
        GridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepDots(current = page)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceDark)
                        .border(0.5.dp, BorderDark, RoundedCornerShape(20.dp))
                        .rippleClickable { onNavigateToDashboard() }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .visible(page != 3)
                ) {
                    Text(
                        text = stringResource(R.string.common_skip),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = AppFont
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 130.dp),
                userScrollEnabled = false
            ) { p ->
                // Wraps its text, so the pager grows with the font scale and the board keeps the rest.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val titles = listOf(
                        stringResource(R.string.tutorial_1_title), stringResource(R.string.tutorial_2_title),
                        stringResource(R.string.tutorial_3_title), stringResource(R.string.tutorial_4_title),
                    )
                    val descriptions = listOf(
                        stringResource(R.string.tutorial_1_body),
                        stringResource(R.string.tutorial_2_body),
                        stringResource(R.string.tutorial_3_body),
                        stringResource(R.string.tutorial_4_body)
                    )
                    Text(
                        text = stringResource(R.string.tutorial_step, p + 1, 4),
                        color = NeonCyan.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp,
                        fontFamily = AppFont
                    )
                    Text(
                        text = titles[p],
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = descriptions[p],
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = AppFont,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when (page) {
                        0 -> Step1Content(grid1, handX.value) {
                            if (selectedTool == Tool.FILL) {
                                val nextIdx = grid1.indexOfFirst { it == CellState.EMPTY }
                                if (nextIdx != -1) {
                                    grid1[nextIdx] = CellState.CYAN
                                    if (grid1.all { it == CellState.CYAN }) {
                                        scope.launch { delay(600.milliseconds); pagerState.animateScrollToPage(1) }
                                    }
                                }
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                        1 -> Step2Content(grid2) { idx ->
                            if (selectedTool != Tool.CROSS || idx != 1) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            } else {
                                grid2[1] = CellState.CROSS
                                scope.launch { delay(800.milliseconds); pagerState.animateScrollToPage(2) }
                            }
                        }
                        2 -> Step3Content(grid3, handX.value) {
                            val nextEmptyIdx = grid3.indexOfFirst { it == CellState.EMPTY }
                            if (nextEmptyIdx != -1) {
                                val requiredTool = if (nextEmptyIdx < 3) Tool.PURPLE else Tool.GOLD
                                if (selectedTool == requiredTool) {
                                    grid3[nextEmptyIdx] = if (requiredTool == Tool.PURPLE) CellState.PURPLE else CellState.GOLD
                                    if (grid3.all { it != CellState.EMPTY }) {
                                        scope.launch { delay(600.milliseconds); pagerState.animateScrollToPage(3) }
                                    }
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                        }
                        3 -> Step4Content()
                    }
                }
            }

            if (page == 3) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF4CAF50))
                        .rippleClickable(onClick = {
                            onNavigateToDashboard()
                        }),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.common_start),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 50.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolButton(Tool.CROSS, selectedTool == Tool.CROSS, Color.Transparent) { selectedTool = Tool.CROSS }
                    Spacer(Modifier.width(16.dp))
                    ToolButton(Tool.FILL, selectedTool == Tool.FILL, NeonCyan) { selectedTool = Tool.FILL }
                    if (page >= 2) {
                        Spacer(Modifier.width(16.dp))
                        ToolButton(Tool.PURPLE, selectedTool == Tool.PURPLE, PurpleAccent) { selectedTool = Tool.PURPLE }
                        Spacer(Modifier.width(16.dp))
                        ToolButton(Tool.GOLD, selectedTool == Tool.GOLD, GameGold) { selectedTool = Tool.GOLD }
                    }
                }
            }
        }
    }
}

@Composable
private fun Step1Content(grid: List<CellState>, handOffset: Float, onFill: () -> Unit) {
    val clueLabel = stringResource(R.string.tutorial_row_clue, "4")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("4", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier
            .padding(end = 16.dp)
            .clearAndSetSemantics { contentDescription = clueLabel })
        Box {
            Text("👆", fontSize = 30.sp, modifier = Modifier
                .clearAndSetSemantics { }
                .offset { IntOffset(handOffset.roundToInt(), 40) }
                .zIndex(2f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val idx = (change.position.x / 54.dp.toPx()).toInt().coerceIn(0, 4)
                        val currentCount = grid.count { it != CellState.EMPTY }
                        if (idx == currentCount) onFill()
                    }
                }
            ) {
                grid.forEachIndexed { i, state ->
                    // Tapping the next empty cell fills it, like dragging onto it.
                    CellView(state, i, onClick = { if (i == grid.count { it != CellState.EMPTY }) onFill() })
                }
            }
        }
    }
}

@Composable
private fun Step2Content(grid: List<CellState>, onCross: (Int) -> Unit) {
    val clueLabel = stringResource(R.string.tutorial_row_clue, "1, 3")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier
            .padding(end = 16.dp)
            .clearAndSetSemantics { contentDescription = clueLabel }) {
            Text("1", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("3", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            grid.forEachIndexed { i, state ->
                CellView(state, i, onClick = { onCross(i) })
            }
        }
    }
}

@Composable
private fun Step3Content(grid: List<CellState>, handOffset: Float, onFill: () -> Unit) {
    val clueLabel = stringResource(
        R.string.tutorial_row_clue,
        stringResource(R.string.board_clue_part, 3, stringResource(R.string.color_purple)) + ", " +
            stringResource(R.string.board_clue_part, 2, stringResource(R.string.color_gold))
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier
            .padding(end = 16.dp)
            .clearAndSetSemantics { contentDescription = clueLabel }) {
            Text("3", color = PurpleAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("2", color = GameGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Box {
            Text("👆", fontSize = 30.sp, modifier = Modifier
                .clearAndSetSemantics { }
                .offset { IntOffset(handOffset.roundToInt(), 40) }
                .zIndex(2f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val idx = (change.position.x / 54.dp.toPx()).toInt().coerceIn(0, 4)
                        val currentCount = grid.count { it != CellState.EMPTY }
                        if (idx == currentCount) onFill()
                    }
                }
            ) {
                grid.forEachIndexed { i, state ->
                    // Tapping the next empty cell fills it, like dragging onto it.
                    CellView(state, i, onClick = { if (i == grid.count { it != CellState.EMPTY }) onFill() })
                }
            }
        }
    }
}

@Composable
private fun Step4Content() {
    val checkData = listOf(
        "00000000000000",
        "00000000000000",
        "00000000000100",
        "00000000001110",
        "00000000011111",
        "00100000111110",
        "01110001111100",
        "11111011111000",
        "01111111110000",
        "00111111100000",
        "00011111000000",
        "00001110000000",
        "00000100000000",
        "00000000000000"
    )

    val green = Color(0xFF4CAF50)
    val rowClues = listOf("", "", "1", "3", "5", "1 5", "3 5", "5 5", "9", "7", "5", "3", "1", "")
    val colClues = listOf("1", "3", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "3", "1")

    val finishedLabel = stringResource(R.string.tutorial_finished_puzzle)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clearAndSetSemantics { contentDescription = finishedLabel }
    ) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 4.dp)) {
            Box(Modifier.size(16.dp))
            rowClues.forEach { clue ->
                Text(clue, color = green, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.height(19.dp))
            }
        }
        Column {
            Row {
                colClues.forEach { clue ->
                    Column(modifier = Modifier.width(19.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        clue.split(" ").forEach {
                            Text(it, color = green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                checkData.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                        row.forEach { char ->
                            Box(
                                Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (char == '1') green else SurfaceDark)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** A practice cell, read as "Square 2, empty"; tappable when [onClick] is set (the drag steps also fill by tap). */
@Composable
private fun CellView(state: CellState, index: Int, onClick: (() -> Unit)? = null) {
    val stateLabel = stringResource(
        when (state) {
            CellState.EMPTY -> R.string.board_empty
            CellState.CYAN -> R.string.board_filled
            CellState.PURPLE -> R.string.color_purple
            CellState.GOLD -> R.string.color_gold
            CellState.CROSS -> R.string.board_crossed
        }
    )
    val cellLabel = stringResource(R.string.tutorial_square_state, index + 1, stateLabel)
    Box(
        Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when (state) {
                    CellState.EMPTY -> SurfaceDark
                    CellState.CYAN -> NeonCyan
                    CellState.PURPLE -> PurpleAccent
                    CellState.GOLD -> GameGold
                    CellState.CROSS -> SurfaceDark
                }
            )
            .border(0.5.dp, BorderDark, RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
            .clearAndSetSemantics {
                contentDescription = cellLabel
                if (onClick != null) this.onClick { onClick(); true }
            },
        contentAlignment = Alignment.Center
    ) {
        if (state == CellState.CROSS) {
            Text("✕", color = DangerRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ToolButton(tool: Tool, isSelected: Boolean, color: Color, onClick: () -> Unit) {
    val label = stringResource(
        when (tool) {
            Tool.FILL -> R.string.game_fill
            Tool.CROSS -> R.string.game_cross_tool
            Tool.PURPLE -> R.string.tutorial_tool_purple
            Tool.GOLD -> R.string.tutorial_tool_gold
        }
    )
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent)
            .border(
                2.dp,
                if (isSelected) (if (color == Color.Transparent) TextPrimary else color) else BorderDark,
                CircleShape
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = label
                selected = isSelected
                role = Role.RadioButton
                this.onClick { onClick(); true }
            },
        contentAlignment = Alignment.Center
    ) {
        when (tool) {
            Tool.FILL, Tool.PURPLE, Tool.GOLD -> {
                Box(Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (tool == Tool.FILL) NeonCyan else if (tool == Tool.PURPLE) PurpleAccent else GameGold))
            }
            Tool.CROSS -> {
                Text("✕", color = DangerRed, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StepDots(count: Int = 4, current: Int) {
    // The step is read from the "ADIM x / 4" label, so the dots are decoration.
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.clearAndSetSemantics { }) {
        repeat(count) { i ->
            Box(
                Modifier
                    .size(if (i == current) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i == current) NeonCyan else SurfaceVariantDark)
            )
        }
    }
}

private fun Modifier.rippleClickable(onClick: () -> Unit): Modifier = rippleClickable(Role.Button, onClick)

private fun Modifier.rippleClickable(role: Role?, onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = Color(0xFF0D0D10)),
        role = role,
        onClick = onClick
    )
}
