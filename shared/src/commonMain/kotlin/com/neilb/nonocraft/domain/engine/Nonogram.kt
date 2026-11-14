package com.neilb.nonocraft.domain.engine

import com.neilb.nonocraft.domain.model.Clue


/**
 * Immutable puzzle definition. `solution[r][c]` is 0 for empty or a 1-based palette index.
 * `palette[0]` is the background colour, `palette[i]` the colour of index i.
 */
class Nonogram(
    val id: String,
    val title: String,
    val solution: List<List<Int>>,
    val palette: List<String> = listOf("#FFFFFF", "#000000"),
    val maxLives: Int = 3,
) {
    val rows: Int = solution.size
    val cols: Int = solution.firstOrNull()?.size ?: 0
    val rowClues: List<List<Clue>> = solution.map { cluesOf(it) }
    val colClues: List<List<Clue>> = (0 until cols).map { c -> cluesOf(solution.map { it[c] }) }
    val colorCount: Int = solution.flatten().maxOrNull() ?: 0
    val isMultiColor: Boolean get() = colorCount > 1
    val filledCellCount: Int = solution.sumOf { row -> row.count { it > 0 } }

    init {
        require(rows > 0 && solution.all { it.size == cols }) { "solution must be a non-empty rectangle" }
    }

    companion object {
        fun cluesOf(line: List<Int>): List<Clue> {
            val out = ArrayList<Clue>()
            var count = 0
            var color = 0
            for (v in line) {
                if (v > 0 && v == color) {
                    count++
                } else {
                    if (count > 0) out += Clue(count, color)
                    count = if (v > 0) 1 else 0
                    color = v
                }
            }
            if (count > 0) out += Clue(count, color)
            return out
        }
    }
}
