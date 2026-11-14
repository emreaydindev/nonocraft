package com.neilb.nonocraft.presentation.screens.settings.subscreens.theme

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
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = TextPrimary)
                }
                Text(
                    text = "Uygulama Teması",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
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
                    SectionLabel(text = "Standart (Ücretsiz)", modifier = Modifier.padding(start = 4.dp))
                    DashboardCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
                        Column {
                            ThemeOptionRow("Sistem Teması", "system", selectedTheme, false) { selectedTheme = "system" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow("Karanlık Mod", "dark", selectedTheme, false) { selectedTheme = "dark" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow("Aydınlık Mod", "light", selectedTheme, false) { selectedTheme = "light" }
                        }
                    }
                }

                // PREMIUM TEMALAR
                Column {
                    SectionLabel(text = "Özel Koleksiyon (Premium)", modifier = Modifier.padding(start = 4.dp))
                    DashboardCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
                        Column {
                            ThemeOptionRow("AMOLED Siyah", "amoled", selectedTheme, true) { selectedTheme = "amoled" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow("Retro Wave", "retro", selectedTheme, true) { selectedTheme = "retro" }
                            Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(BorderDark))
                            ThemeOptionRow("Cyberpunk", "cyberpunk", selectedTheme, true) { selectedTheme = "cyberpunk" }
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
    val isSelected = themeId == selectedId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .rippleClickable(onClick)
            .background(if (isSelected) NeonCyan.copy(alpha = 0.05f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (isPremium) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = GameGold, modifier = Modifier.size(20.dp))
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