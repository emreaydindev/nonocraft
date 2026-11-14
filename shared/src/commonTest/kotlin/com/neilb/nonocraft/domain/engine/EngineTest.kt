package com.neilb.nonocraft.domain.engine

import com.neilb.nonocraft.domain.model.Clue

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EngineTest {
    private val heart = listOf(
        listOf(0, 1, 0, 1, 0),
        listOf(1, 1, 1, 1, 1),
        listOf(1, 1, 1, 1, 1),
        listOf(0, 1, 1, 1, 0),
        listOf(0, 0, 1, 0, 0),
    )

    @Test fun cluesAreComputed() {
        val p = Nonogram("h", "Heart", heart)
        assertEquals(listOf(Clue(1, 1), Clue(1, 1)), p.rowClues[0])
        assertEquals(listOf(Clue(5, 1)), p.rowClues[1])
        assertEquals(listOf(Clue(2, 1)), p.colClues[0])
        assertEquals(listOf(Clue(4, 1)), p.colClues[1])
    }

    @Test fun multiColorClues() {
        assertEquals(listOf(Clue(1, 1), Clue(2, 2)), Nonogram.cluesOf(listOf(1, 2, 2, 0)))
    }

    @Test fun lineSolverOverlap() {
        val poss = LineSolver.solve(IntArray(5) { -1 }, listOf(Clue(4, 1)))!!
        assertEquals(setOf(1), poss[2])
        assertEquals(setOf(0, 1), poss[0])
        assertNull(LineSolver.solve(intArrayOf(1, 1, 1), listOf(Clue(1, 1))))
    }

    @Test fun ambiguityIsDetected() {
        assertFalse(LineSolver.isLineSolvable(listOf(listOf(1, 0), listOf(0, 1))))
        assertTrue(LineSolver.isLineSolvable(heart))
    }

    @Test fun correctMovesWin() {
        var s = GameState.start(Nonogram("h", "Heart", heart))
        for (r in 0 until 5) for (c in 0 until 5) if (heart[r][c] == 1) s = GameRules.apply(s, r, c)
        assertEquals(GameStatus.WON, s.status)
        assertEquals(3, s.stars)
        assertEquals(1f, s.progress)
    }

    @Test fun mistakesCostLivesAndAreCorrected() {
        var s = GameState.start(Nonogram("h", "Heart", heart, maxLives = 2))
        s = GameRules.apply(s, 0, 0) // fill on empty -> mistake
        assertEquals(1, s.livesLeft)
        assertEquals(Mark.Crossed, s.marks[0][0])
        s = GameRules.apply(s, 0, 2)
        assertEquals(GameStatus.OUT_OF_LIVES, s.status)
        s = GameRules.revive(s)
        assertEquals(GameStatus.PLAYING, s.status)
        assertEquals(1, s.livesLeft)
    }

    @Test fun unlimitedLivesNeverLose() {
        var s = GameState.start(Nonogram("h", "Heart", heart, maxLives = 1), unlimitedLives = true)
        repeat(3) { s = GameRules.apply(s, 0, it * 2) }
        assertEquals(GameStatus.PLAYING, s.status)
        assertEquals(3, s.mistakes)
    }

    @Test fun completedRowIsAutoCrossed() {
        var s = GameState.start(Nonogram("h", "Heart", heart))
        s = GameRules.apply(s, 4, 2)
        assertEquals(Mark.Crossed, s.marks[4][0])
        assertEquals(Mark.Crossed, s.marks[4][4])
    }

    @Test fun hintRevealsDeducibleCell() {
        val s = GameState.start(Nonogram("h", "Heart", heart))
        assertNotNull(GameRules.findDeducibleCell(s))
        val h = GameRules.hint(s)
        assertEquals(1, h.hintsUsed)
        assertTrue(h.correctFilled == 1 || h.status == GameStatus.WON)
    }

    @Test fun hintsSolveEverything() {
        var s = GameState.start(Nonogram("h", "Heart", heart))
        repeat(30) { s = GameRules.hint(s) }
        assertEquals(GameStatus.WON, s.status)
        assertEquals(1, s.stars)
    }

    @Test fun strokeLocksToAxis() {
        val st = Stroke(2, 2)
        assertEquals(listOf(2 to 3), st.moveTo(3, 3).let { if (it.isEmpty()) st.moveTo(2, 3) else it })
        assertEquals(listOf(2 to 4), st.moveTo(4, 4))
    }

    @Test fun generatorProducesUniquePuzzles() {
        for (size in listOf(5, 10, 15)) {
            val a = Generator.generate(size, 42L + size)
            assertTrue(LineSolver.isLineSolvable(a))
            assertEquals(a, Generator.generate(size, 42L + size))
        }
    }
}
