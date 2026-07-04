package com.neilb.nonocraft.presentation.screens.settings.subscreens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardCard
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit
) {
    var dailyPuzzle by remember { mutableStateOf(true) }
    var newFollowers by remember { mutableStateOf(true) }
    var likesAndComments by remember { mutableStateOf(true) }
    var announcements by remember { mutableStateOf(false) }

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
                Text("Bildirim Tercihleri", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                DashboardCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 20) {
                    Column {
                        NotificationSwitchRow("Günün Bulmacası Hatırlatıcısı", dailyPuzzle) { dailyPuzzle = it }
                        HorizontalDivider(color = BorderDark, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        NotificationSwitchRow("Yeni Takipçiler", newFollowers) { newFollowers = it }
                        HorizontalDivider(color = BorderDark, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        NotificationSwitchRow("Beğeni ve Yorumlar", likesAndComments) { likesAndComments = it }
                        HorizontalDivider(color = BorderDark, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        NotificationSwitchRow("Duyurular ve Etkinlikler", announcements) { announcements = it }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSwitchRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = AppFont)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BackgroundDark,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceVariantDark,
                uncheckedBorderColor = BorderDark
            )
        )
    }
}