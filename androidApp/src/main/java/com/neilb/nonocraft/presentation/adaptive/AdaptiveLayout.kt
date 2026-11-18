package com.neilb.nonocraft.presentation.adaptive

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

/**
 * Medium and expanded window widths (600 dp and up): tablets, unfolded foldables, Chromebooks and wide
 * multi-window / freeform windows. These get the navigation rail and the two-pane layouts; compact
 * windows keep the phone layouts.
 */
@Composable
fun isWideWindow(): Boolean =
    currentWindowAdaptiveInfo().windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

/** List pane width of the two-pane layouts; the same 360 pt column as on iOS. */
val ListPaneWidth: Dp = 360.dp

/** Widest a single column of cards or text should get, so lines stay readable on big screens. */
val ReadableWidth: Dp = 640.dp

/**
 * Table-top posture: a foldable half opened with a horizontal hinge, like a small laptop. [hingeBounds]
 * is the hinge in window coordinates (px), so a layout can put content above it and below it.
 */
data class TableTopPosture(val hingeBounds: Rect)

/**
 * The current table-top posture from [WindowInfoTracker], or null when the device is flat, closed, a
 * phone without a hinge, or the hinge is vertical (book posture).
 */
@Composable
fun rememberTableTopPosture(): TableTopPosture? {
    val activity = LocalContext.current.findActivity()
    val flow = remember(activity) {
        if (activity == null) {
            emptyFlow()
        } else {
            WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity).map { info ->
                info.displayFeatures
                    .filterIsInstance<FoldingFeature>()
                    .firstOrNull {
                        it.state == FoldingFeature.State.HALF_OPENED &&
                            it.orientation == FoldingFeature.Orientation.HORIZONTAL
                    }
                    ?.let { TableTopPosture(it.bounds) }
            }
        }
    }
    val posture by flow.collectAsState(initial = null)
    return posture
}

/** Window height in dp that a px value in window coordinates corresponds to. */
@Composable
fun Int.pxToDp(): Dp = with(LocalDensity.current) { this@pxToDp.toDp() }

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * A column that is centred when its content fits and scrolls when it doesn't (large font scales, short
 * windows, folded outer screens). The Android counterpart of the iOS `CenteredScrollView`.
 */
@Composable
fun CenteredScrollColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}

/** Whether the user picked one of the largest font scales (150 % and up), where rows turn into columns. */
@Composable
fun isLargeFontScale(): Boolean = LocalDensity.current.fontScale >= 1.5f

/**
 * A row that becomes a column at the largest font scales, where its pieces no longer fit side by side.
 * The Android counterpart of the iOS `AdaptiveStack`.
 */
@Composable
fun AdaptiveRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    columnSpacing: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    if (isLargeFontScale()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(columnSpacing),
            horizontalAlignment = Alignment.Start
        ) { content() }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.CenterVertically
        ) { content() }
    }
}
