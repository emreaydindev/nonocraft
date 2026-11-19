package com.neilb.nonocraft.presentation.mapper

import com.neilb.nonocraft.presentation.model.Difficulty
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PuzzleUiMapperTest {

    @Test
    fun parsesApiTimestamps() {
        val expected = 1_794_909_600_000L // 2026-11-17T10:00:00Z
        assertEquals(expected, "2026-11-17T10:00:00.000Z".toEpochMillisOrNull())
        assertEquals(expected, "2026-11-17T10:00:00Z".toEpochMillisOrNull())
        assertEquals(expected, "2026-11-17 10:00:00".toEpochMillisOrNull())
        assertEquals(expected, "2026-11-17T13:00:00+03:00".toEpochMillisOrNull())
        assertNull("yesterday".toEpochMillisOrNull())
    }

    @Test
    fun parsesDifficulty() {
        assertEquals(Difficulty.EASY, Difficulty.fromApi("easy"))
        assertEquals(Difficulty.HARD, Difficulty.fromApi("HARD"))
        assertNull(Difficulty.fromApi("Orta"))
        assertNull(Difficulty.fromApi(null))
    }
}
