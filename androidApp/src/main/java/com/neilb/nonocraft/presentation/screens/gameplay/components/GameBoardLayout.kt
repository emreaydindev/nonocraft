package com.neilb.nonocraft.presentation.screens.gameplay.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.neilb.nonocraft.presentation.adaptive.pxToDp
import com.neilb.nonocraft.presentation.adaptive.rememberTableTopPosture

/**
 * Arranges the game board and its controls for the space the screen actually gets (not the device):
 * - table-top posture (foldable half open, hinge horizontal): board above the hinge, controls below it;
 * - clearly wider than tall (tablet landscape, unfolded foldable, Chromebook, wide freeform window):
 *   board and controls side by side;
 * - otherwise (phones, portrait): board above the controls.
 * [controls] gets whether it sits beside the board.
 */
@Composable
internal fun GameBoardLayout(
    board: @Composable (Modifier) -> Unit,
    controls: @Composable (isSideBySide: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val tableTop = rememberTableTopPosture()
    var topInWindow by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier.onGloballyPositioned { topInWindow = it.positionInWindow().y }
    ) {
        // Less margin on small screens (folded outer displays) so the clues get the room.
        val boardPadding = if (minOf(maxWidth, maxHeight) < 420.dp) 8.dp else 16.dp
        val heightPx = constraints.maxHeight.toFloat()
        val controlsWidth = minOf(420.dp, maxWidth * 0.38f)
        val hingeTop = tableTop?.let { it.hingeBounds.top - topInWindow }
        val hingeBottom = tableTop?.let { it.hingeBounds.bottom - topInWindow }

        when {
            hingeTop != null && hingeBottom != null && hingeTop > 0f && hingeBottom < heightPx -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    board(
                        Modifier
                            .testTag(BoardTag)
                            .height(hingeTop.toInt().pxToDp())
                            .fillMaxWidth()
                            .padding(boardPadding)
                    )
                    Spacer(Modifier.height((hingeBottom - hingeTop).toInt().pxToDp()))
                    Box(
                        modifier = Modifier
                            .testTag(ControlsTag)
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        controls(false)
                    }
                }
            }

            maxWidth >= 700.dp && maxWidth > maxHeight * 1.15f -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    board(Modifier.testTag(BoardTag).weight(1f).fillMaxHeight().padding(boardPadding))
                    Box(
                        modifier = Modifier
                            .testTag(ControlsTag)
                            .width(controlsWidth)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        controls(true)
                    }
                }
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    board(Modifier.testTag(BoardTag).weight(1f).fillMaxWidth().padding(boardPadding))
                    Box(modifier = Modifier.testTag(ControlsTag)) {
                        controls(false)
                    }
                }
            }
        }
    }
}

internal const val BoardTag = "game-board"
internal const val ControlsTag = "game-controls"
