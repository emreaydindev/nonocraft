package com.neilb.nonocraft.presentation.screens.settings.subscreens.icons

import androidx.compose.ui.semantics.contentDescription
import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
import com.neilb.nonocraft.presentation.theme.*

data class AppIconOption(val id: String, val name: String, val isPremium: Boolean)

@Composable
fun AppIconScreen(
    onNavigateBack: () -> Unit,
    hasPremium: Boolean = false
) {
    var selectedIcon by remember { mutableStateOf("classic") }

    val icons = listOf(
        AppIconOption("classic", stringResource(R.string.icon_classic), false),
        AppIconOption("dark", stringResource(R.string.icon_dark), false),
        AppIconOption("neon", stringResource(R.string.icon_neon), true),
        AppIconOption("gold", stringResource(R.string.icon_gold), true),
        AppIconOption("pixel", stringResource(R.string.icon_pixel), true),
        AppIconOption("synth", stringResource(R.string.icon_synth), true)
    )

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark).navigationBarsPadding()) {
        GridBackground()

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.common_back), tint = TextPrimary)
                }
                Text(
                    text = stringResource(R.string.settings_icon),
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    modifier = Modifier.semantics { heading() }
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(18.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(icons) { iconOpt ->
                    AppIconCard(
                        option = iconOpt,
                        isSelected = selectedIcon == iconOpt.id,
                        hasPremium = hasPremium,
                        onClick = {
                            if (!iconOpt.isPremium || hasPremium) {
                                selectedIcon = iconOpt.id
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppIconCard(
    option: AppIconOption,
    isSelected: Boolean,
    hasPremium: Boolean,
    onClick: () -> Unit
) {
    val premiumLabel = stringResource(R.string.common_premium)
    val lockedLabel = stringResource(R.string.common_locked)
    val isLocked = option.isPremium && !hasPremium

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) NeonCyan.copy(alpha = 0.1f) else SurfaceDark)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonCyan else BorderDark,
                shape = RoundedCornerShape(20.dp)
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .clearAndSetSemantics {
                contentDescription = listOfNotNull(
                    option.name,
                    premiumLabel.takeIf { option.isPremium },
                    lockedLabel.takeIf { isLocked },
                ).joinToString(", ")
            }
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (option.id == "gold") GameGold else PurpleAccent)
                .border(1.dp, BorderDark, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isLocked) {
                Box(
                    modifier = Modifier.fillMaxSize().background(BackgroundDark.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Lock, contentDescription = lockedLabel, tint = TextMuted, modifier = Modifier.size(28.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = option.name,
            color = if (isSelected) NeonCyan else TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = AppFont,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isSelected) {
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(NeonCyan), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = BackgroundDark, modifier = Modifier.size(16.dp))
            }
        } else if (option.isPremium) {
            Text(text = stringResource(R.string.common_premium_badge), color = GameGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
        } else {
            Box(modifier = Modifier.size(24.dp))
        }
    }
}