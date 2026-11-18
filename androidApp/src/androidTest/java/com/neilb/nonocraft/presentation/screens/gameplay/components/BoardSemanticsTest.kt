package com.neilb.nonocraft.presentation.screens.gameplay.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.neilb.nonocraft.domain.model.Clue
import com.neilb.nonocraft.domain.model.Puzzle
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** TalkBack reads every clue and cell on the drawn board, and the cell actions reach the game. */
@RunWith(AndroidJUnit4::class)
class BoardSemanticsTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val moves = mutableListOf<String>()
    private val actions = object : BoardCellActions {
        override fun fill(row: Int, col: Int, colorIndex: Int) { moves += "fill $row $col $colorIndex" }
        override fun cross(row: Int, col: Int) { moves += "cross $row $col" }
        override fun clear(row: Int, col: Int) { moves += "clear $row $col" }
    }

    private fun setBoard(palette: List<String>, userGrid: List<List<Int>>) {
        val puzzle = Puzzle(
            id = "p", title = "Test", description = null, userId = "u", username = null, size = 3,
            paletteHex = palette, grid = List(3) { List(3) { 1 } }, type = null, difficulty = null,
            isPublic = true, likeCount = 0, commentCount = 0, playCount = 0, isLiked = false,
            isCompleted = false, createdAt = null, dailyDate = null
        )
        composeRule.setContent {
            GameGridArea(
                puzzle = puzzle,
                userGrid = userGrid,
                rowClues = listOf(listOf(Clue(1, 1), Clue(1, 2)), listOf(Clue(3, 1)), emptyList()),
                colClues = List(3) { listOf(Clue(2, 1)) },
                cursorRow = 0,
                cursorCol = 0,
                onCellClick = { r, c -> moves += "tap $r $c" },
                isCursorVisible = false,
                modifier = Modifier.fillMaxSize(),
                cellActions = actions
            )
        }
    }

    private fun customActionLabels(description: String): List<String> =
        composeRule.onNodeWithContentDescription(description).fetchSemanticsNode()
            .config.getOrElse(SemanticsActions.CustomActions) { emptyList() }.map { it.label }

    private fun runCustomAction(description: String, label: String) {
        val node = composeRule.onNodeWithContentDescription(description).fetchSemanticsNode()
        composeRule.runOnUiThread {
            node.config[SemanticsActions.CustomActions].first { it.label == label }.action()
        }
        composeRule.waitForIdle()
    }

    @Test
    fun cluesAndCellsReadAsPhrases() {
        setBoard(listOf("#FF0000", "#0000FF"), listOf(listOf(0, -1, 2), List(3) { 0 }, List(3) { 0 }))
        composeRule.onNodeWithContentDescription("Satır 1: 1 kırmızı, 1 mavi").assertExists()
        composeRule.onNodeWithContentDescription("Satır 3: boş").assertExists()
        composeRule.onNodeWithContentDescription("Sütun 2: 2 kırmızı").assertExists()
        composeRule.onNodeWithContentDescription("Satır 1, sütun 1")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "boş"))
        composeRule.onNodeWithContentDescription("Satır 1, sütun 2")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "çarpı"))
        composeRule.onNodeWithContentDescription("Satır 1, sütun 3")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "dolu, renk 2, mavi"))
    }

    @Test
    fun emptyCellOffersFillPerColourAndCross() {
        setBoard(listOf("#FF0000", "#0000FF"), listOf(listOf(0, -1, 2), List(3) { 0 }, List(3) { 0 }))
        assertEquals(
            listOf("Doldur: renk 1, kırmızı", "Doldur: renk 2, mavi", "Çarpı koy"),
            customActionLabels("Satır 1, sütun 1")
        )
        assertEquals(listOf("Doldur: renk 1, kırmızı", "Doldur: renk 2, mavi", "Temizle"), customActionLabels("Satır 1, sütun 2"))
        assertEquals(emptyList<String>(), customActionLabels("Satır 1, sütun 3"))

        runCustomAction("Satır 1, sütun 1", "Doldur: renk 2, mavi")
        runCustomAction("Satır 1, sütun 1", "Çarpı koy")
        runCustomAction("Satır 1, sütun 2", "Temizle")
        composeRule.onNodeWithContentDescription("Satır 2, sütun 3").performClick()
        assertEquals(listOf("fill 0 0 2", "cross 0 0", "clear 0 1", "tap 1 2"), moves)
    }

    @Test
    fun singleColourPuzzleSaysFillAndDoesNotNameTheColour() {
        setBoard(listOf("#FFFFFF"), listOf(listOf(1, 0, 0), List(3) { 0 }, List(3) { 0 }))
        composeRule.onNodeWithContentDescription("Satır 1: 1, 1").assertExists()
        composeRule.onNodeWithContentDescription("Satır 1, sütun 1")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "dolu"))
        assertEquals(listOf("Doldur", "Çarpı koy"), customActionLabels("Satır 1, sütun 2"))
    }
}
