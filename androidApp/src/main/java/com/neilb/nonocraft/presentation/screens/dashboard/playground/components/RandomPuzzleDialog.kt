package com.neilb.nonocraft.presentation.screens.dashboard.playground.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neilb.nonocraft.presentation.adaptive.isLargeFontScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.neilb.nonocraft.presentation.screens.auth.components.rippleClickable
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary

@Composable
fun RandomPuzzleDialog(
    onDismiss: () -> Unit,
    onGenerate: (isColored: Boolean, size: String, lives: Int) -> Unit
) {
    var isColored by remember { mutableStateOf(false) }
    var selectedSize by remember { mutableStateOf("10×10") }
    var selectedLives by remember { mutableIntStateOf(3) }

    val livesOptions = listOf(1, 2, 3, 4, 5)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rastgele Oluştur",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont,
                        modifier = Modifier.semantics { heading() }
                    )
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantDark)
                            .rippleClickable(onDismiss)
                            .semantics { contentDescription = "Kapat" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DialogSectionHeader(icon = Icons.Rounded.ColorLens, title = "Oyun Modu")
                    Row(
                        modifier = Modifier.fillMaxWidth().selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SelectableOptionCard(
                            text = "Klasik",
                            isSelected = !isColored,
                            onClick = { isColored = false },
                            modifier = Modifier.weight(1f)
                        )
                        SelectableOptionCard(
                            text = "Renkli",
                            isSelected = isColored,
                            onClick = { isColored = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                val sizes = listOf("5×5", "6×6", "8×8", "9×9",
                                    "10×10", "12×12", "15×15", "20×20")

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DialogSectionHeader(icon = Icons.Rounded.GridOn, title = "Matris Boyutu")
                    // Four per row; two at the largest font scales so "20×20" stays on one line.
                    val perRow = if (isLargeFontScale()) 2 else 4
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.selectableGroup()) {
                        sizes.chunked(perRow).forEach { rowSizes ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowSizes.forEach { size ->
                                    SelectableChip(
                                        text = size,
                                        label = size.replace("×", " x "),
                                        isSelected = selectedSize == size,
                                        onClick = { selectedSize = size },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DialogSectionHeader(icon = Icons.Rounded.Favorite, title = "Can Sayısı")
                        if (selectedLives == 1) {
                            Text(
                                text = "Hardcore",
                                color = DangerRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFont
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().selectableGroup(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        livesOptions.forEach { life ->
                            SelectableChip(
                                text = life.toString(),
                                label = if (life == 1) "1 can, Hardcore" else "$life can",
                                isSelected = selectedLives == life,
                                onClick = { selectedLives = life },
                                activeColor = if (life == 1) DangerRed else PurpleAccent,
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PurpleAccent)
                        .rippleClickable {
                            onGenerate(isColored, selectedSize, selectedLives)
                            onDismiss()
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Oluştur ve Başla",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogSectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = AppFont,
            modifier = Modifier.semantics { heading() }
        )
    }
}

@Composable
private fun SelectableOptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PurpleAccent.copy(alpha = 0.15f) else SurfaceVariantDark)
            .border(
                1.dp,
                if (isSelected) PurpleAccent else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) PurpleAccent else TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont
        )
    }
}

@Composable
private fun SelectableChip(
    text: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = PurpleAccent,
) {
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) activeColor else SurfaceVariantDark)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = label
                selected = isSelected
                role = Role.RadioButton
                onClick { onClick(); true }
            }
            .padding(horizontal = 13.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont
        )
    }
}