package com.neilb.nonocraft.presentation.screens.gameplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.Clue
import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.domain.use_case.puzzle.CompletePuzzle
import com.neilb.nonocraft.domain.use_case.puzzle.GetPuzzle
import com.neilb.nonocraft.domain.use_case.puzzle.PlayPuzzle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

sealed class GameEvent {
    data object PlayCorrectSound : GameEvent()
    data object PlayErrorSound : GameEvent()
    data object PlayVictoryAnimation : GameEvent()
    data object TriggerErrorHaptic : GameEvent()
    data object TriggerTickHaptic : GameEvent()
}

data class GameUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val puzzle: Puzzle? = null,
    val rowClues: List<List<Clue>> = emptyList(),
    val colClues: List<List<Clue>> = emptyList(),
    val userGrid: List<List<Int>> = emptyList(),
    val currentLives: Int = 3,
    val hintsRemaining: Int = 3,
    val timeElapsed: Long = 0L,
    val selectedColorIndex: Int = 1,
    val isCrossMode: Boolean = false,
    val cursorRow: Int = 0,
    val cursorCol: Int = 0,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val isCursorVisible: Boolean = false
)

class GameViewModel(
    savedStateHandle: SavedStateHandle,
    private val getPuzzle: GetPuzzle,
    private val playPuzzle: PlayPuzzle,
    private val completePuzzle: CompletePuzzle
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<GameEvent>()
    val event = _event.asSharedFlow()

    private var timerJob: Job? = null
    private var cursorHideJob: Job? = null
    private var isPlaceHeld = false
    private var isPlaceBlocked = false

    init {
        val gameId: String? = savedStateHandle["gameId"]
        if (gameId != null) loadGame(gameId)
        else _state.update { it.copy(isLoading = false, errorMessage = "Puzzle ID bulunamadı") }
    }

    fun setAnalogFocus(isFocused: Boolean) {
        cursorHideJob?.cancel()
        if (isFocused) {
            _state.update { it.copy(isCursorVisible = true) }
        } else {
            cursorHideJob = viewModelScope.launch {
                delay(3000.milliseconds)
                _state.update { it.copy(isCursorVisible = false) }
            }
        }
    }

    fun setPlacePressed(isPressed: Boolean) {
        isPlaceHeld = isPressed
        if (isPressed) {
            isPlaceBlocked = false
            onActionPressed()
        }
    }

    private fun loadGame(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getPuzzle(id)) {
                is Resource.Success -> {
                    val puzzle = result.data!!
                    val emptyGrid = List(puzzle.size) { List(puzzle.size) { 0 } }
                    val rClues = puzzle.grid.map { generateClues(it) }
                    val cClues = (0 until puzzle.size).map { col ->
                        generateClues(puzzle.grid.map { it[col] })
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            puzzle = puzzle,
                            userGrid = emptyGrid,
                            rowClues = rClues,
                            colClues = cClues,
                            currentLives = 3,
                            hintsRemaining = 3,
                            timeElapsed = 0L,
                            cursorRow = puzzle.size / 2,
                            cursorCol = puzzle.size / 2
                        )
                    }
                    startTimer()
                    playPuzzle(id) // Register play session
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                is Resource.Loading -> { /* handled above */ }
            }
        }
    }

    private fun generateClues(line: List<Int>): List<Clue> {
        val clues = mutableListOf<Clue>()
        var count = 0
        var currentColor = 0
        for (cell in line) {
            if (cell > 0) {
                if (currentColor == cell) count++
                else {
                    if (count > 0) clues.add(Clue(count, currentColor))
                    currentColor = cell; count = 1
                }
            } else {
                if (count > 0) clues.add(Clue(count, currentColor))
                count = 0; currentColor = 0
            }
        }
        if (count > 0) clues.add(Clue(count, currentColor))
        return if (clues.isEmpty()) listOf(Clue(0, 0)) else clues
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (!state.value.isGameOver && !state.value.isVictory) {
                delay(1000L.milliseconds)
                _state.update { cs ->
                    val nt = cs.timeElapsed + 1
                    val tl: Long? = null // puzzles don't have time limit from API
                    if (tl != null && nt >= tl) {
                        timerJob?.cancel(); cs.copy(timeElapsed = nt, isGameOver = true)
                    } else cs.copy(timeElapsed = nt)
                }
            }
        }
    }

    fun useHint() {
        val cs = _state.value
        if (cs.hintsRemaining <= 0 || cs.isGameOver || cs.isVictory || cs.puzzle == null) return
        val puzzleGrid = cs.puzzle.grid
        val currentGrid = cs.userGrid.map { it.toMutableList() }
        val missing = mutableListOf<Pair<Int, Int>>()
        for (r in puzzleGrid.indices) for (c in puzzleGrid[r].indices) {
            if (puzzleGrid[r][c] > 0 && currentGrid[r][c] != puzzleGrid[r][c]) missing.add(Pair(r, c))
        }
        if (missing.isNotEmpty()) {
            val target = missing.random()
            currentGrid[target.first][target.second] = puzzleGrid[target.first][target.second]
            _state.update { it.copy(userGrid = currentGrid, hintsRemaining = it.hintsRemaining - 1) }
            viewModelScope.launch { _event.emit(GameEvent.PlayCorrectSound) }
            checkVictory(currentGrid, puzzleGrid)
        }
    }

    fun toggleCrossMode(isCross: Boolean) { _state.update { it.copy(isCrossMode = isCross) } }
    fun selectColor(colorIndex: Int) { _state.update { it.copy(selectedColorIndex = colorIndex, isCrossMode = false) } }
    fun onActionPressed() { handleCellInteraction(_state.value.cursorRow, _state.value.cursorCol) }

    fun onCellClicked(row: Int, col: Int) {
        _state.update { it.copy(cursorRow = row, cursorCol = col) }
        handleCellInteraction(row, col)
    }

    private fun checkVictory(userGrid: List<List<Int>>, puzzleGrid: List<List<Int>>) {
        for (i in puzzleGrid.indices) for (j in puzzleGrid[i].indices) {
            if (puzzleGrid[i][j] > 0 && userGrid[i][j] != puzzleGrid[i][j]) return
        }
        timerJob?.cancel()
        _state.update { it.copy(isVictory = true) }
        viewModelScope.launch { 
            _event.emit(GameEvent.PlayVictoryAnimation)
            _state.value.puzzle?.id?.let { completePuzzle(it) } // Register completion
        }
    }

    fun moveCursor(dRow: Int, dCol: Int) {
        val cs = _state.value; val size = cs.puzzle?.size ?: return
        val nr = (cs.cursorRow + dRow).coerceIn(0, size - 1)
        val nc = (cs.cursorCol + dCol).coerceIn(0, size - 1)
        if (nr != cs.cursorRow || nc != cs.cursorCol) {
            _state.update { it.copy(cursorRow = nr, cursorCol = nc) }
            viewModelScope.launch { _event.emit(GameEvent.TriggerTickHaptic) }
            if (isPlaceHeld && !isPlaceBlocked) onActionPressed()
        }
    }

    private fun handleCellInteraction(row: Int, col: Int) {
        val cs = _state.value
        if (cs.isGameOver || cs.isVictory || cs.puzzle == null) return
        val puzzleGrid = cs.puzzle.grid
        val currentGrid = cs.userGrid.map { it.toMutableList() }
        val targetValue = puzzleGrid[row][col]
        val currentValue = currentGrid[row][col]
        if (currentValue != 0 && currentValue != -1) return
        if (cs.isCrossMode) {
            currentGrid[row][col] = if (currentValue == 0) -1 else 0
            _state.update { it.copy(userGrid = currentGrid) }
            viewModelScope.launch { _event.emit(GameEvent.TriggerTickHaptic) }
        } else {
            val selectedColor = cs.selectedColorIndex
            if (targetValue == selectedColor) {
                currentGrid[row][col] = selectedColor
                _state.update { it.copy(userGrid = currentGrid) }
                viewModelScope.launch { _event.emit(GameEvent.PlayCorrectSound) }
                checkVictory(currentGrid, puzzleGrid)
            } else {
                isPlaceBlocked = true
                val newLives = cs.currentLives - 1
                currentGrid[row][col] = -1
                viewModelScope.launch {
                    _event.emit(GameEvent.TriggerErrorHaptic)
                    _event.emit(GameEvent.PlayErrorSound)
                }
                if (newLives <= 0) {
                    timerJob?.cancel()
                    _state.update { it.copy(userGrid = currentGrid, currentLives = 0, isGameOver = true) }
                } else {
                    _state.update { it.copy(userGrid = currentGrid, currentLives = newLives) }
                }
            }
        }
    }
}
