package com.neilb.nonocraft.domain.engine

/**
 * DP line solver shared by hints, validation and the procedural generator.
 * Cell encoding: -1 unknown, 0 empty, >0 colour index.
 * Mirrors `nonocraft_backend/src/shared/solvability.ts` so client and server agree.
 */
object LineSolver {
    const val UNKNOWN = -1

    /** For every cell, the set of values still possible; null when the line is contradictory. */
    fun solve(line: IntArray, clues: List<Clue>): Array<MutableSet<Int>>? {
        val n = line.size
        val k = clues.size
        val memo = HashMap<Int, Boolean>()
        fun key(p: Int, j: Int) = p * (k + 1) + j
        fun fits(p: Int, j: Int): Boolean {
            val (count, color) = clues[j]
            if (p + count > n) return false
            for (i in p until p + count) if (line[i] != UNKNOWN && line[i] != color) return false
            return true
        }
        fun needsGap(j: Int) = j + 1 < k && clues[j + 1].color == clues[j].color
        fun canEmpty(p: Int) = p < n && (line[p] == UNKNOWN || line[p] == 0)

        fun feasible(p: Int, j: Int): Boolean {
            if (p == n) return j == k
            val kk = key(p, j)
            memo[kk]?.let { return it }
            var ok = canEmpty(p) && feasible(p + 1, j)
            if (!ok && j < k && fits(p, j)) {
                val next = p + clues[j].count
                ok = if (needsGap(j)) canEmpty(next) && feasible(next + 1, j + 1) else feasible(next, j + 1)
            }
            memo[kk] = ok
            return ok
        }

        if (!feasible(0, 0)) return null
        val possible = Array(n) { mutableSetOf<Int>() }
        val visited = HashSet<Int>()
        val stack = ArrayDeque<Pair<Int, Int>>()
        stack.addLast(0 to 0)
        while (stack.isNotEmpty()) {
            val (p, j) = stack.removeLast()
            if (p == n || !visited.add(key(p, j))) continue
            if (canEmpty(p) && feasible(p + 1, j)) {
                possible[p] += 0
                stack.addLast(p + 1 to j)
            }
            if (j < k && fits(p, j)) {
                val (count, color) = clues[j]
                val next = p + count
                if (needsGap(j)) {
                    if (canEmpty(next) && feasible(next + 1, j + 1)) {
                        for (i in p until next) possible[i] += color
                        possible[next] += 0
                        stack.addLast(next + 1 to j + 1)
                    }
                } else if (feasible(next, j + 1)) {
                    for (i in p until next) possible[i] += color
                    stack.addLast(next to j + 1)
                }
            }
        }
        return possible
    }

    data class GridResult(val consistent: Boolean, val solved: Boolean, val grid: Array<IntArray>) {
        val determinedRatio: Double
            get() {
                val total = grid.sumOf { it.size }.coerceAtLeast(1)
                return grid.sumOf { row -> row.count { it != UNKNOWN } }.toDouble() / total
            }
    }

    /** Propagates line logic over the whole grid starting from [start] (or all unknown). */
    fun solveGrid(rowClues: List<List<Clue>>, colClues: List<List<Clue>>, start: Array<IntArray>? = null): GridResult {
        val rows = rowClues.size
        val cols = colClues.size
        val g = start?.map { it.copyOf() }?.toTypedArray() ?: Array(rows) { IntArray(cols) { UNKNOWN } }
        val dirtyRows = (0 until rows).toMutableSet()
        val dirtyCols = (0 until cols).toMutableSet()
        var guard = 0
        while ((dirtyRows.isNotEmpty() || dirtyCols.isNotEmpty()) && guard++ < 10_000) {
            for (r in dirtyRows.toList()) {
                dirtyRows.remove(r)
                val poss = solve(g[r], rowClues[r]) ?: return GridResult(false, false, g)
                for (c in 0 until cols) if (g[r][c] == UNKNOWN && poss[c].size == 1) {
                    g[r][c] = poss[c].first(); dirtyCols += c
                }
            }
            for (c in dirtyCols.toList()) {
                dirtyCols.remove(c)
                val col = IntArray(rows) { g[it][c] }
                val poss = solve(col, colClues[c]) ?: return GridResult(false, false, g)
                for (r in 0 until rows) if (g[r][c] == UNKNOWN && poss[r].size == 1) {
                    g[r][c] = poss[r].first(); dirtyRows += r
                }
            }
        }
        val solved = g.all { row -> row.none { it == UNKNOWN } }
        return GridResult(true, solved, g)
    }

    /** True when a human can solve the puzzle with pure line logic (implies a unique answer). */
    fun isLineSolvable(solution: List<List<Int>>): Boolean {
        val p = Nonogram("check", "", solution)
        return solveGrid(p.rowClues, p.colClues).solved
    }
}
