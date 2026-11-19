package com.neilb.nonocraft.presentation.screens.gameplay

import com.neilb.nonocraft.presentation.screens.gameplay.components.rememberColorPatternSetting
import com.neilb.nonocraft.presentation.text.ErrorContext
import com.neilb.nonocraft.presentation.text.message
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import kotlinx.coroutines.delay
import com.neilb.nonocraft.presentation.screens.gameplay.components.BoardCellActions
import com.neilb.nonocraft.presentation.screens.gameplay.components.BoardSpeech
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neilb.nonocraft.presentation.screens.gameplay.components.AnalogStick
import com.neilb.nonocraft.presentation.screens.gameplay.components.GameActionButtons
import com.neilb.nonocraft.presentation.screens.gameplay.components.GameBoardLayout
import com.neilb.nonocraft.presentation.screens.gameplay.components.GameGridArea
import com.neilb.nonocraft.presentation.screens.gameplay.components.ToolPaletteBar
import com.neilb.nonocraft.presentation.screens.gameplay.components.toColor
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary
import kotlin.math.max
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.neilb.nonocraft.presentation.adaptive.pxToDp
import com.neilb.nonocraft.presentation.adaptive.rememberTableTopPosture

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is GameEvent.TriggerErrorHaptic -> {
                    // Yanlış yapıldığında ağır titreşim
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                is GameEvent.TriggerTickHaptic -> {
                    // D-Pad ile gezinirken hafif titreşim
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                is GameEvent.PlayErrorSound -> {
                    // TODO: Medya oynatıcı ile "hata" sesi çalınacak
                }
                is GameEvent.PlayCorrectSound -> {
                    // TODO: Medya oynatıcı ile "doğru" (pop) sesi çalınacak
                }
                is GameEvent.PlayVictoryAnimation -> {
                    // TODO: Başarı sesi çalınacak ve ekrana Lottie animasyonu fırlatılacak
                    // (Örneğin üstüne bir Overlay açılıp yıldızlar patlayabilir)
                }
            }
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // TalkBack hears a lost heart as it happens; the win and game-over dialogs announce themselves.
    var announcement by remember { mutableStateOf("") }
    var previousLives by remember { mutableIntStateOf(-1) }
    LaunchedEffect(state.currentLives) {
        if (previousLives > state.currentLives && state.currentLives > 0) {
            announcement = "Yanlış hamle, ${state.currentLives} can kaldı"
        }
        previousLives = state.currentLives
    }
    // Cleared after it is spoken so it does not linger as a stop at the top of the screen.
    LaunchedEffect(announcement) {
        if (announcement.isNotEmpty()) {
            delay(4000)
            announcement = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .navigationBarsPadding()
            .onPreviewKeyEvent { handleGameKey(it, state, viewModel) }
    ) {
        GridBackground()

        // Keyboard focus sits on this hidden node so the screen keeps getting key events; on the root it
        // would make TalkBack read the clues, hearts and stick as one stop.
        Box(
            Modifier
                .size(1.dp)
                .clearAndSetSemantics { }
                .focusRequester(focusRequester)
                .focusable()
        )

        Box(
            Modifier
                .size(1.dp)
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    if (announcement.isNotEmpty()) contentDescription = announcement
                }
        )

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = NeonCyan
            )
        } else if (state.error != null) {
            Text(
                text = state.error!!.message(ErrorContext.GAME),
                color = DangerRed,
                fontFamily = AppFont,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
            ) {
                GameTopBar(
                    title = state.puzzle?.title ?: "Yükleniyor...",
                    timeLimitSeconds = state.puzzle?.timeLimitSeconds,
                    timeElapsed = state.timeElapsed,
                    onBackClick = onNavigateBack
                )

                LivesBar(
                    currentLives = state.currentLives,
                    maxLives = state.puzzle?.maxLives ?: 3
                )

                GameContent(
                    state = state,
                    viewModel = viewModel,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }

        if (state.isVictory) {
            GameEndOverlay(
                title = "TEBRİKLER!",
                subtitle = "Bulmacayı başarıyla çözdün.",
                buttonText = "Devam Et",
                isVictory = true,
                onAction = onNavigateBack
            )
        }

        if (state.isGameOver) {
            GameEndOverlay(
                title = "OYUN BİTTİ",
                subtitle = "Tüm canların tükendi.",
                buttonText = "Tekrar Dene",
                isVictory = false,
                onAction = onNavigateBack // Or implement retry
            )
        }
    }
}

/**
 * Board and controls in [GameBoardLayout]. The board is movable content, so switching arrangement on a
 * resize, fold or unfold only rescales it.
 */
@Composable
private fun GameContent(
    state: GameUiState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val cellActions = remember(viewModel) { ViewModelCellActions(viewModel) }
    val colorPatterns = rememberColorPatternSetting()
    val board = remember(viewModel) {
        movableContentOf { boardState: GameUiState, boardModifier: Modifier ->
            Box(modifier = boardModifier, contentAlignment = Alignment.Center) {
                val puzzle = boardState.puzzle
                if (puzzle != null) {
                    GameGridArea(
                        puzzle = puzzle,
                        userGrid = boardState.userGrid,
                        rowClues = boardState.rowClues,
                        colClues = boardState.colClues,
                        cursorRow = boardState.cursorRow,
                        cursorCol = boardState.cursorCol,
                        isCursorVisible = boardState.isCursorVisible,
                        onCellClick = { row, col -> viewModel.onCellClicked(row, col) },
                        cellActions = cellActions,
                        showColorPatterns = colorPatterns.isEnabled
                    )
                }
            }
        }
    }

    GameBoardLayout(
        modifier = modifier,
        board = { boardModifier -> board(state, boardModifier) },
        controls = { isSideBySide -> GameControlsPanel(state = state, viewModel = viewModel, isSideBySide = isSideBySide, showColorPatterns = colorPatterns.isEnabled) }
    )
}

/** Stick, hint and place buttons, then the colour / cross palette. Stacked when beside the board. */
@Composable
private fun GameControlsPanel(
    state: GameUiState,
    viewModel: GameViewModel,
    isSideBySide: Boolean,
    showColorPatterns: Boolean
) {
    val activeColor = state.puzzle?.paletteHex?.getOrNull(state.selectedColorIndex - 1)?.toColor() ?: NeonCyan
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = if (isSideBySide) Arrangement.SpaceEvenly else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val palette = state.puzzle?.paletteHex.orEmpty()
            AnalogStick(
                onMove = { dRow, dCol -> viewModel.moveCursor(dRow, dCol) },
                onFocusChange = { isFocused -> viewModel.setAnalogFocus(isFocused) },
                cursorDescription = BoardSpeech.cellPosition(state.cursorRow, state.cursorCol) + ", " +
                    BoardSpeech.cellState(state.userGrid.getOrNull(state.cursorRow)?.getOrNull(state.cursorCol) ?: 0, palette)
            )

            GameActionButtons(
                onPlacePressed = { isPressed -> viewModel.setPlacePressed(isPressed) },
                onHintAction = { viewModel.useHint() },
                hintsRemaining = state.hintsRemaining,
                selectedColor = activeColor,
                isCrossMode = state.isCrossMode
            )
        }

        val palette = state.puzzle?.paletteHex.orEmpty()
        ToolPaletteBar(
            paletteColors = palette.map { it.toColor() },
            showColorPatterns = showColorPatterns,
            colorLabels = palette.indices.map { index ->
                if (palette.size > 1) BoardSpeech.colorLabel(index + 1, palette).replaceFirstChar { it.uppercase() } else "Doldur"
            },
            selectedColorIndex = state.selectedColorIndex,
            isCrossMode = state.isCrossMode,
            onColorSelected = { viewModel.selectColor(it) },
            onCrossSelected = { viewModel.toggleCrossMode(true) }
        )
    }
}

/**
 * Hardware keyboard (tablets, Chromebooks, keyboard covers): arrows or WASD move the cursor, Space or
 * Enter places (held, it keeps placing while moving, like the place button), X picks the cross,
 * 1–9 pick a palette colour and H uses a hint.
 */
private fun handleGameKey(event: KeyEvent, state: GameUiState, viewModel: GameViewModel): Boolean {
    val isDown = event.type == KeyEventType.KeyDown
    val isUp = event.type == KeyEventType.KeyUp
    val move = when (event.key) {
        Key.DirectionUp, Key.W -> -1 to 0
        Key.DirectionDown, Key.S -> 1 to 0
        Key.DirectionLeft, Key.A -> 0 to -1
        Key.DirectionRight, Key.D -> 0 to 1
        else -> null
    }
    if (move != null) {
        if (isDown) {
            viewModel.setAnalogFocus(true)
            viewModel.moveCursor(move.first, move.second)
        } else if (isUp) {
            viewModel.setAnalogFocus(false)
        }
        return true
    }
    return when (event.key) {
        Key.Spacebar, Key.Enter, Key.NumPadEnter -> {
            if (isDown && event.nativeKeyEvent.repeatCount == 0) {
                viewModel.setAnalogFocus(true)
                viewModel.setPlacePressed(true)
            } else if (isUp) {
                viewModel.setPlacePressed(false)
                viewModel.setAnalogFocus(false)
            }
            true
        }
        Key.X -> { if (isDown) viewModel.toggleCrossMode(true); true }
        Key.Backspace, Key.Delete -> {
            if (isDown) ViewModelCellActions(viewModel).clear(state.cursorRow, state.cursorCol)
            true
        }
        Key.H -> { if (isDown && event.nativeKeyEvent.repeatCount == 0) viewModel.useHint(); true }
        else -> {
            val digit = event.nativeKeyEvent.unicodeChar.toChar().digitToIntOrNull()
            val paletteSize = state.puzzle?.paletteHex?.size ?: 0
            if (digit != null && digit in 1..paletteSize) {
                if (isDown) viewModel.selectColor(digit)
                true
            } else {
                false
            }
        }
    }
}

@Composable
fun GameEndOverlay(
    title: String,
    subtitle: String,
    buttonText: String,
    isVictory: Boolean,
    onAction: () -> Unit
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                // Swallows taps around the card without becoming an unlabeled TalkBack button.
                .pointerInput(Unit) { detectTapGestures { } }
                // TalkBack announces the dialog by its title ("Tebrikler!", "Oyun bitti") when it opens.
                .semantics { paneTitle = "$title. $subtitle" },
            color = Color.Black.copy(alpha = 0.85f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.semantics { heading() },
                        color = if (isVictory) NeonCyan else DangerRed,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = AppFont,
                        letterSpacing = 4.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontFamily = AppFont,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(48.dp))
                    Button(
                        onClick = onAction,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVictory) NeonCyan else SurfaceDark,
                            contentColor = if (isVictory) BackgroundDark else TextPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .widthIn(min = 200.dp)
                            .heightIn(min = 56.dp)
                    ) {
                        Text(
                            text = buttonText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFont
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}

@Composable
private fun GameTopBar(
    title: String,
    timeLimitSeconds: Long?,
    timeElapsed: Long,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Geri",
                tint = TextPrimary
            )
        }

        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() }
        )

        // Grows with the font scale so the timer is never clipped; 48 dp keeps the title centred.
        Box(
            modifier = Modifier.widthIn(min = 48.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (timeLimitSeconds != null) {
                val remaining = max(0L, timeLimitSeconds - timeElapsed)
                Text(
                    text = formatTime(remaining),
                    modifier = Modifier.clearAndSetSemantics {
                        contentDescription = "Kalan süre: ${remaining / 60} dakika ${remaining % 60} saniye"
                    },
                    color = DangerRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
            }
        }
    }
}

@Composable
private fun LivesBar(
    currentLives: Int,
    maxLives: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clearAndSetSemantics { contentDescription = "$currentLives can kaldı" },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxLives) { index ->
            val hasLife = index < currentLives
            Icon(
                imageVector = if (hasLife) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "Life",
                tint = if (hasLife) DangerRed else DangerRed.copy(alpha = 0.3f),
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .size(24.dp)
            )
        }
    }
}
/**
 * Single-cell moves for TalkBack and the keyboard, made from the view model's own moves (pick a tool, touch
 * the cell); the player's tool is put back afterwards. Crossing a crossed cell clears it.
 */
private class ViewModelCellActions(private val viewModel: GameViewModel) : BoardCellActions {
    override fun fill(row: Int, col: Int, colorIndex: Int) {
        val before = viewModel.state.value
        viewModel.selectColor(colorIndex)
        viewModel.onCellClicked(row, col)
        if (before.isCrossMode) viewModel.toggleCrossMode(true)
        else if (before.selectedColorIndex != colorIndex) viewModel.selectColor(before.selectedColorIndex)
    }

    override fun cross(row: Int, col: Int) {
        if (cellValue(row, col) == 0) withCrossTool { viewModel.onCellClicked(row, col) }
    }

    override fun clear(row: Int, col: Int) {
        if (cellValue(row, col) == -1) withCrossTool { viewModel.onCellClicked(row, col) }
    }

    private fun cellValue(row: Int, col: Int): Int? = viewModel.state.value.userGrid.getOrNull(row)?.getOrNull(col)

    private inline fun withCrossTool(move: () -> Unit) {
        val wasCrossMode = viewModel.state.value.isCrossMode
        viewModel.toggleCrossMode(true)
        move()
        if (!wasCrossMode) viewModel.toggleCrossMode(false)
    }
}
