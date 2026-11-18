package com.neilb.nonocraft.presentation.screens.gameplay.components

import android.graphics.Rect
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.window.layout.FoldingFeature
import androidx.window.testing.layout.FoldingFeature
import androidx.window.testing.layout.TestWindowLayoutInfo
import androidx.window.testing.layout.WindowLayoutInfoPublisherRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith

/** The game board and controls on each side of a table-top hinge, published through WindowInfoTracker. */
@RunWith(AndroidJUnit4::class)
class GameBoardLayoutTest {

    private val publisherRule = WindowLayoutInfoPublisherRule()
    private val composeRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val rules: TestRule = RuleChain.outerRule(publisherRule).around(composeRule)

    private fun setLayout() {
        composeRule.setContent {
            GameBoardLayout(
                modifier = Modifier.fillMaxSize(),
                board = { modifier -> Box(modifier) },
                controls = { Box(Modifier.size(200.dp, 120.dp)) }
            )
        }
    }

    @Test
    fun tableTopPutsBoardAboveAndControlsBelowTheHinge() {
        setLayout()
        val activity = composeRule.activity
        val hinge = FoldingFeature(
            activity = activity,
            state = FoldingFeature.State.HALF_OPENED,
            orientation = FoldingFeature.Orientation.HORIZONTAL
        )
        publisherRule.overrideWindowLayoutInfo(TestWindowLayoutInfo(listOf(hinge)))
        composeRule.waitForIdle()

        val density = activity.resources.displayMetrics.density
        val rootTop = IntArray(2).also { activity.window.decorView.rootView.getLocationInWindow(it) }[1]
        val hingeTopDp = (hinge.bounds.top - rootTop) / density
        val hingeBottomDp = (hinge.bounds.bottom - rootTop) / density

        val board = composeRule.onNodeWithTag(BoardTag).getBoundsInRoot()
        val controls = composeRule.onNodeWithTag(ControlsTag).getBoundsInRoot()
        assertTrue("board bottom ${board.bottom} above hinge $hingeTopDp", board.bottom.value <= hingeTopDp + 1)
        assertTrue("controls top ${controls.top} below hinge $hingeBottomDp", controls.top.value >= hingeBottomDp - 1)
    }

    @Test
    fun flatDeviceKeepsBoardAboveControlsInPortrait() {
        setLayout()
        publisherRule.overrideWindowLayoutInfo(TestWindowLayoutInfo(emptyList()))
        composeRule.waitForIdle()

        val board = composeRule.onNodeWithTag(BoardTag).getBoundsInRoot()
        val controls = composeRule.onNodeWithTag(ControlsTag).getBoundsInRoot()
        assertTrue(board.bottom <= controls.top)
    }
}
