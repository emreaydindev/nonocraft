package com.neilb.nonocraft.domain.engine

import kotlin.random.Random

/**
 * Offline puzzle generator (used for "Endless" mode and as a fallback when the API is unreachable).
 * Same algorithm as the backend: smoothed random blob + greedy repair until line-solvable.
 */
object Generator {
    fun generate(size: Int, seed: Long, maxAttempts: Int = 40): List<List<Int>> {
        val rnd = Random(seed)
        var best: Array<IntArray>? = null
        var bestRatio = -1.0
        repeat(maxAttempts) {
            val grid = blob(size, rnd, 0.5 + rnd.nextDouble() * 0.15)
            repeat(size * size) {
                val p = Nonogram("gen", "", grid.map { it.toList() })
                val res = LineSolver.solveGrid(p.rowClues, p.colClues)
                if (res.solved) return grid.map { it.toList() }
                if (res.determinedRatio > bestRatio) {
                    bestRatio = res.determinedRatio
                    best = grid.map { it.copyOf() }.toTypedArray()
                }
                val unknown = ArrayList<Pair<Int, Int>>()
                res.grid.forEachIndexed { r, row -> row.forEachIndexed { c, v -> if (v == LineSolver.UNKNOWN) unknown += r to c } }
                val (r, c) = unknown[rnd.nextInt(unknown.size)]
                grid[r][c] = if (grid[r][c] > 0) 0 else 1
            }
        }
        val b = best!!.map { it.toList() }
        check(LineSolver.isLineSolvable(b)) { "generation failed" }
        return b
    }

    private fun blob(size: Int, rnd: Random, density: Double): Array<IntArray> {
        var g = Array(size) { IntArray(size) { if (rnd.nextDouble() < density) 1 else 0 } }
        if (rnd.nextDouble() < 0.6) for (row in g) for (c in 0 until size / 2) row[size - 1 - c] = row[c]
        repeat(2) {
            val prev = g
            g = Array(size) { r ->
                IntArray(size) { c ->
                    var n = 0
                    for (dr in -1..1) for (dc in -1..1) {
                        if (dr == 0 && dc == 0) continue
                        n += prev.getOrNull(r + dr)?.getOrNull(c + dc) ?: 0
                    }
                    when {
                        n >= 5 -> 1
                        n <= 2 -> 0
                        else -> prev[r][c]
                    }
                }
            }
        }
        return g
    }
}
