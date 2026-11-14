package com.neilb.nonocraft.domain.engine

/** What the player has put in a cell. */
sealed interface Mark {
    data object Empty : Mark
    data class Filled(val color: Int) : Mark
    data object Crossed : Mark
}

enum class Tool { FILL, CROSS }

enum class GameStatus { PLAYING, WON, OUT_OF_LIVES }

data class GameState(
    val puzzle: Nonogram,
    val marks: List<List<Mark>>,
    val tool: Tool = Tool.FILL,
    val selectedColor: Int = 1,
    val livesLeft: Int,
    /** Pro users / endless-practice: mistakes are counted but never end the game. */
    val unlimitedLives: Boolean = false,
    val mistakes: Int = 0,
    val hintsUsed: Int = 0,
    val elapsedSeconds: Int = 0,
    val status: GameStatus = GameStatus.PLAYING,
    /** Cells revealed by a mistake or hint (rendered differently). */
    val revealed: Set<Int> = emptySet(),
    val lastMistakeCell: Int? = null,
) {
    val rows get() = puzzle.rows
    val cols get() = puzzle.cols
    fun index(r: Int, c: Int) = r * cols + c

    val correctFilled: Int
        get() = marks.indices.sumOf { r -> marks[r].indices.count { c -> (marks[r][c] as? Mark.Filled)?.color == puzzle.solution[r][c] && puzzle.solution[r][c] > 0 } }

    val progress: Float get() = if (puzzle.filledCellCount == 0) 1f else correctFilled.toFloat() / puzzle.filledCellCount

    /** 3 = flawless, 2 = ≤ 2 mistakes and no hints, 1 = solved. */
    val stars: Int
        get() = when {
            status != GameStatus.WON -> 0
            mistakes == 0 && hintsUsed == 0 -> 3
            mistakes <= 2 && hintsUsed == 0 -> 2
            else -> 1
        }

    fun isRowComplete(r: Int) = (0 until cols).all { c -> cellDone(r, c) }
    fun isColComplete(c: Int) = (0 until rows).all { r -> cellDone(r, c) }
    private fun cellDone(r: Int, c: Int): Boolean {
        val s = puzzle.solution[r][c]
        return if (s == 0) true else (marks[r][c] as? Mark.Filled)?.color == s
    }

    companion object {
        fun start(puzzle: Nonogram, unlimitedLives: Boolean = false) = GameState(
            puzzle = puzzle,
            marks = List(puzzle.rows) { List(puzzle.cols) { Mark.Empty } },
            livesLeft = puzzle.maxLives,
            unlimitedLives = unlimitedLives,
        )
    }
}

/**
 * Pure game rules ("lives" mode, as in the most popular nonogram apps):
 * a wrong move costs a life and the cell is corrected automatically, so the board
 * never contains a wrong mark. Completed rows/columns are auto-crossed.
 */
object GameRules {

    fun selectTool(s: GameState, tool: Tool) = s.copy(tool = tool)

    fun selectColor(s: GameState, color: Int) = s.copy(selectedColor = color.coerceIn(1, maxOf(1, s.puzzle.colorCount)), tool = Tool.FILL)

    fun tick(s: GameState) = if (s.status == GameStatus.PLAYING) s.copy(elapsedSeconds = s.elapsedSeconds + 1) else s

    /** Applies the current tool to one cell. Already-decided cells are left untouched. */
    fun apply(s: GameState, r: Int, c: Int): GameState {
        if (s.status != GameStatus.PLAYING) return s
        if (r !in 0 until s.rows || c !in 0 until s.cols) return s
        if (s.marks[r][c] != Mark.Empty) return s

        val truth = s.puzzle.solution[r][c]
        val wanted: Mark = if (s.tool == Tool.FILL) Mark.Filled(s.selectedColor) else Mark.Crossed
        val correctMark: Mark = if (truth == 0) Mark.Crossed else Mark.Filled(truth)
        val isCorrect = wanted == correctMark

        var next = s.copy(marks = s.marks.set(r, c, correctMark), lastMistakeCell = null)
        if (!isCorrect) {
            val idx = s.index(r, c)
            val lives = if (s.unlimitedLives) s.livesLeft else s.livesLeft - 1
            next = next.copy(
                mistakes = s.mistakes + 1,
                livesLeft = lives,
                revealed = s.revealed + idx,
                lastMistakeCell = idx,
                status = if (!s.unlimitedLives && lives <= 0) GameStatus.OUT_OF_LIVES else GameStatus.PLAYING,
            )
        }
        next = autoCross(next, r, c)
        return checkWin(next)
    }

    /** Continue after running out of lives (rewarded ad, crystals or Pro). */
    fun revive(s: GameState, lives: Int = 1): GameState =
        if (s.status == GameStatus.OUT_OF_LIVES) s.copy(status = GameStatus.PLAYING, livesLeft = lives) else s

    /**
     * Smart hint: finds a cell that line logic can deduce from what is already on the
     * board (teaches the player), falling back to any unsolved filled cell.
     */
    fun hint(s: GameState): GameState {
        if (s.status != GameStatus.PLAYING) return s
        val target = findDeducibleCell(s) ?: firstUnsolvedCell(s) ?: return s
        val (r, c) = target
        val truth = s.puzzle.solution[r][c]
        val mark: Mark = if (truth == 0) Mark.Crossed else Mark.Filled(truth)
        val next = s.copy(
            marks = s.marks.set(r, c, mark),
            hintsUsed = s.hintsUsed + 1,
            revealed = s.revealed + s.index(r, c),
        )
        return checkWin(autoCross(next, r, c))
    }

    fun findDeducibleCell(s: GameState): Pair<Int, Int>? {
        val known = Array(s.rows) { r -> IntArray(s.cols) { c -> encode(s.marks[r][c]) } }
        for (r in 0 until s.rows) {
            val poss = LineSolver.solve(known[r], s.puzzle.rowClues[r]) ?: continue
            for (c in 0 until s.cols) if (known[r][c] == LineSolver.UNKNOWN && poss[c].size == 1 && s.puzzle.solution[r][c] > 0) return r to c
        }
        for (c in 0 until s.cols) {
            val col = IntArray(s.rows) { known[it][c] }
            val poss = LineSolver.solve(col, s.puzzle.colClues[c]) ?: continue
            for (r in 0 until s.rows) if (col[r] == LineSolver.UNKNOWN && poss[r].size == 1 && s.puzzle.solution[r][c] > 0) return r to c
        }
        return null
    }

    private fun firstUnsolvedCell(s: GameState): Pair<Int, Int>? {
        for (r in 0 until s.rows) for (c in 0 until s.cols)
            if (s.puzzle.solution[r][c] > 0 && s.marks[r][c] == Mark.Empty) return r to c
        return null
    }

    private fun encode(m: Mark) = when (m) {
        Mark.Empty -> LineSolver.UNKNOWN
        Mark.Crossed -> 0
        is Mark.Filled -> m.color
    }

    private fun autoCross(s: GameState, r: Int, c: Int): GameState {
        var marks = s.marks
        if (s.isRowComplete(r)) for (cc in 0 until s.cols) if (marks[r][cc] == Mark.Empty) marks = marks.set(r, cc, Mark.Crossed)
        val tmp = s.copy(marks = marks)
        if (tmp.isColComplete(c)) for (rr in 0 until s.rows) if (marks[rr][c] == Mark.Empty) marks = marks.set(rr, c, Mark.Crossed)
        return s.copy(marks = marks)
    }

    private fun checkWin(s: GameState): GameState {
        if (s.status == GameStatus.OUT_OF_LIVES) return s
        val won = (0 until s.rows).all { s.isRowComplete(it) }
        if (!won) return s
        // Fill the remaining empties with crosses for a clean final picture.
        val marks = s.marks.mapIndexed { r, row -> row.mapIndexed { c, m -> if (m == Mark.Empty && s.puzzle.solution[r][c] == 0) Mark.Crossed else m } }
        return s.copy(marks = marks, status = GameStatus.WON)
    }

    private fun List<List<Mark>>.set(r: Int, c: Int, m: Mark): List<List<Mark>> =
        mapIndexed { rr, row -> if (rr != r) row else row.mapIndexed { cc, old -> if (cc == c) m else old } }
}

/**
 * Drag gesture helper: locks the stroke to the row or column of the first move,
 * like every good nonogram app, so a sloppy swipe never paints a diagonal.
 */
class Stroke(private val startR: Int, private val startC: Int) {
    private var axis: Int = 0 // 0 undecided, 1 row, 2 column
    private val visited = LinkedHashSet<Pair<Int, Int>>().apply { add(startR to startC) }

    /** Returns the new cells to apply when the finger moves to (r, c). */
    fun moveTo(r: Int, c: Int): List<Pair<Int, Int>> {
        if (axis == 0 && (r != startR || c != startC)) axis = if (r == startR) 1 else if (c == startC) 2 else if (kotlin.math.abs(c - startC) >= kotlin.math.abs(r - startR)) 1 else 2
        val (tr, tc) = when (axis) {
            1 -> startR to c
            2 -> r to startC
            else -> return emptyList()
        }
        val out = ArrayList<Pair<Int, Int>>()
        val rRange = if (tr >= startR) startR..tr else tr..startR
        val cRange = if (tc >= startC) startC..tc else tc..startC
        for (rr in rRange) for (cc in cRange) if (visited.add(rr to cc)) out += rr to cc
        return out
    }
}
