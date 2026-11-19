package com.neilb.nonocraft.presentation.screens.settings.subscreens.theme

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.WorkspacePremium
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardCard
import com.neilb.nonocraft.presentation.screens.dashboard.components.SectionLabel
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun ThemeScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf("dark") }

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
                    text = stringResource(R.string.settings_theme),
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    modifier = Modifier.semantics { heading() }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // ÜCRETSİZ TEMALAR
                Column {
                    SectionLabel(text = stringResource(R.string.theme_standard), modifier = Modifier.padding(start = 4.dp))
                    DashboardCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
                        Column {
                            ThemeOptionRow(stringResource(R.string.theme_system), "system", selectedTheme, false) { selectedTheme = "system" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow(stringResource(R.string.theme_dark), "dark", selectedTheme, false) { selectedTheme = "dark" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow(stringResource(R.string.theme_light), "light", selectedTheme, false) { selectedTheme = "light" }
                        }
                    }
                }

                // PREMIUM TEMALAR
                Column {
                    SectionLabel(text = stringResource(R.string.theme_exclusive), modifier = Modifier.padding(start = 4.dp))
                    DashboardCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
                        Column {
                            ThemeOptionRow(stringResource(R.string.theme_amoled), "amoled", selectedTheme, true) { selectedTheme = "amoled" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow(stringResource(R.string.theme_retro), "retro", selectedTheme, true) { selectedTheme = "retro" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow(stringResource(R.string.theme_cyberpunk), "cyberpunk", selectedTheme, true) { selectedTheme = "cyberpunk" }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    themeId: String,
    selectedId: String,
    isPremium: Boolean,
    onClick: () -> Unit
) {
    val premiumTitle = stringResource(R.string.theme_premium_a11y, title)
    val isSelected = themeId == selectedId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .clearAndSetSemantics { contentDescription = if (isPremium) premiumTitle else title }
            .background(if (isSelected) NeonCyan.copy(alpha = 0.05f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (isPremium) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = stringResource(R.string.common_premium), tint = GameGold, modifier = Modifier.size(20.dp))
            }
            Text(
                text = title,
                color = if (isSelected) NeonCyan else TextPrimary,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontFamily = AppFont
            )
        }

        if (isSelected) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, BorderDark, CircleShape)
            )
        }
    }
}