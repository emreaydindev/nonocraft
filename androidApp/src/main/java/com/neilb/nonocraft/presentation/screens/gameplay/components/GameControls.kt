package com.neilb.nonocraft.presentation.screens.gameplay.components

import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.core.graphics.toColorInt
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.ripple

@Composable
fun AnalogStick(
    onMove: (dRow: Int, dCol: Int) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    cursorDescription: String,
    modifier: Modifier = Modifier
) {
    // TalkBack: the stick is the board cursor; actions move it a cell at a time and the place button
    // then fills, crosses or clears the cell under it.
    fun step(dRow: Int, dCol: Int): Boolean {
        onFocusChange(true)
        onMove(dRow, dCol)
        onFocusChange(false)
        return true
    }
    val res = LocalContext.current.resources
    val stickSize = 100.dp
    val thumbSize = 44.dp
    val maxRadius = (stickSize / 2) - (thumbSize / 2)
    val maxRadiusPx = maxRadius.value * 2.5f

    var thumbOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(isDragging, thumbOffset) {
        if (isDragging && thumbOffset.getDistance() > (maxRadiusPx * 0.4f)) {
            while (isActive) {
                val dRow = if (abs(thumbOffset.y) > abs(thumbOffset.x)) {
                    if (thumbOffset.y > 0) 1 else -1
                } else 0

                val dCol = if (abs(thumbOffset.x) >= abs(thumbOffset.y)) {
                    if (thumbOffset.x > 0) 1 else -1
                } else 0

                onMove(dRow, dCol)
                delay(150.milliseconds)
            }
        }
    }

    Box(
        modifier = modifier
            .size(stickSize)
            .clip(CircleShape)
            .background(SurfaceDark.copy(alpha = 0.5f))
            .border(2.dp, BorderDark, CircleShape)
            .clearAndSetSemantics {
                contentDescription = res.getString(R.string.game_cursor)
                stateDescription = cursorDescription
                customActions = listOf(
                    CustomAccessibilityAction(res.getString(R.string.game_move_up)) { step(-1, 0) },
                    CustomAccessibilityAction(res.getString(R.string.game_move_down)) { step(1, 0) },
                    CustomAccessibilityAction(res.getString(R.string.game_move_left)) { step(0, -1) },
                    CustomAccessibilityAction(res.getString(R.string.game_move_right)) { step(0, 1) },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onFocusChange(true) // İmleci Göster
                    },
                    onDragEnd = {
                        isDragging = false
                        thumbOffset = Offset.Zero
                        onFocusChange(false) // 3 saniyelik gizlenme sayacını başlat
                    },
                    onDragCancel = {
                        isDragging = false
                        thumbOffset = Offset.Zero
                        onFocusChange(false)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = thumbOffset + dragAmount
                        thumbOffset = if (newOffset.getDistance() > maxRadiusPx) {
                            newOffset.copy(
                                x = (newOffset.x / newOffset.getDistance()) * maxRadiusPx,
                                y = (newOffset.y / newOffset.getDistance()) * maxRadiusPx
                            )
                        } else {
                            newOffset
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(8.dp).offset(y = -30.dp).clip(CircleShape).background(BorderDark))
        Box(modifier = Modifier.size(8.dp).offset(y = 30.dp).clip(CircleShape).background(BorderDark))
        Box(modifier = Modifier.size(8.dp).offset(x = -30.dp).clip(CircleShape).background(BorderDark))
        Box(modifier = Modifier.size(8.dp).offset(x = 30.dp).clip(CircleShape).background(BorderDark))

        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffset.x.roundToInt(), thumbOffset.y.roundToInt()) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(SurfaceVariantDark)
                .border(1.dp, TextPrimary.copy(alpha = 0.3f), CircleShape)
        )
    }
}

@Composable
fun GameActionButtons(
    onPlacePressed: (Boolean) -> Unit,
    onHintAction: () -> Unit,
    hintsRemaining: Int,
    selectedColor: Color,
    isCrossMode: Boolean,
    modifier: Modifier = Modifier
) {
    val hintLabel = stringResource(R.string.game_hints_left, hintsRemaining)
    val placeLabel = stringResource(if (isCrossMode) R.string.game_place_cross else R.string.game_place_fill)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
                .border(2.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                .rippleClickable { onHintAction() }
                .clearAndSetSemantics {
                    contentDescription = hintLabel
                    role = Role.Button
                    onClick { onHintAction(); true }
                },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                Text(
                    text = hintsRemaining.toString(),
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    fontSize = 18.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(selectedColor.copy(alpha = 0.15f))
                .border(3.dp, selectedColor, CircleShape)
                // Held, it keeps placing while the stick moves; for TalkBack a double tap places once.
                .clearAndSetSemantics {
                    contentDescription = placeLabel
                    role = Role.Button
                    onClick { onPlacePressed(true); onPlacePressed(false); true }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            onPlacePressed(true)
                            tryAwaitRelease()
                            onPlacePressed(false)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Edit, contentDescription = null, tint = selectedColor, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun ToolPaletteBar(
    paletteColors: List<Color>,
    colorLabels: List<String>,
    selectedColorIndex: Int,
    isCrossMode: Boolean,
    onColorSelected: (Int) -> Unit,
    onCrossSelected: () -> Unit,
    modifier: Modifier = Modifier,
    showColorPatterns: Boolean = false,
) {
    // Wraps onto more lines when the palette is wider than the space (narrow outer screens, the
    // control column beside the board).
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ToolItem(
            color = Color.Transparent,
            label = stringResource(R.string.game_cross_tool),
            isCross = true,
            isSelected = isCrossMode,
            onClick = onCrossSelected
        )
        paletteColors.forEachIndexed { index, color ->
            val actualColorIndex = index + 1
            ToolItem(
                color = color,
                label = colorLabels.getOrNull(index) ?: stringResource(R.string.game_color_n, actualColorIndex),
                isCross = false,
                isSelected = !isCrossMode && selectedColorIndex == actualColorIndex,
                onClick = { onColorSelected(actualColorIndex) },
                glyphIndex = actualColorIndex.takeIf { showColorPatterns && paletteColors.size > 1 }
            )
        }
    }
}

@Composable
private fun ToolItem(
    color: Color,
    label: String,
    isCross: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    glyphIndex: Int? = null,
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(if (isSelected) (if (isCross) DangerRed.copy(alpha = 0.2f) else color.copy(alpha = 0.2f)) else Color.Transparent)
            .border(2.dp, if (isSelected) (if (isCross) DangerRed else color) else BorderDark, CircleShape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = label
                selected = isSelected
                role = Role.RadioButton
                onClick { onClick(); true }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isCross) {
            Icon(Icons.Rounded.Close, contentDescription = null, tint = DangerRed, modifier = Modifier.size(28.dp))
        } else {
            val textMeasurer = rememberTextMeasurer()
            val density = LocalDensity.current
            Canvas(
                Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
                    // Dark swatches (e.g. black) vanish on the dark bar without an outline, as on iOS.
                    .then(if (color.luminance() < 0.2f) Modifier.border(1.dp, TextPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp)) else Modifier)
            ) {
                if (glyphIndex != null) {
                    val inset = size.minDimension * 0.22f
                    drawPaletteGlyph(
                        glyphIndex,
                        Rect(Offset(inset, inset), Size(size.width - 2 * inset, size.height - 2 * inset)),
                        PaletteGlyph.contrastOn(color), textMeasurer, Density(density.density, 1f)
                    )
                }
            }
        }
    }
}

fun String.toColor(): Color {
    return try {
        Color(this.toColorInt())
    } catch (e: Exception) {
        Color.Gray
    }
}

private fun Modifier.rippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(interactionSource = remember { MutableInteractionSource() }, indication = ripple(color = Color(0xFF0D0D10)), onClick = onClick)
}