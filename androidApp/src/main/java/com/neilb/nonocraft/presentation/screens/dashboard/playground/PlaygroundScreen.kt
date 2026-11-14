package com.neilb.nonocraft.presentation.screens.dashboard.playground

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neilb.nonocraft.R
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardCard
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
import com.neilb.nonocraft.presentation.screens.dashboard.playground.components.RandomPuzzleDialog
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.GameGold
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PlaygroundScreen(
    onNavigateToGame: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTutorial: () -> Unit,
    viewModel: PlaygroundViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaygroundContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToGame = onNavigateToGame,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToTutorial = onNavigateToTutorial
    )
}

@Composable
fun PlaygroundContent(
    state: PlaygroundUiState,
    onEvent: (PlaygroundUiEvent) -> Unit,
    onNavigateToGame: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTutorial: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
    ) {
        GridBackground()
        WatermarkLogo()

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                PlaygroundHeader(
                    username = state.username,
                    stars = state.stars,
                    crystals = state.crystals,
                    crystalRefillMinutes = state.crystalRefillMinutes,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToTutorial = onNavigateToTutorial
                )

                DailyChallengeCard(
                    isSolved = state.isDailySolved,
                    onPlayToday = { onNavigateToGame("daily-today-20") },
                    onPlayPast = { date -> onNavigateToGame("daily-$date-20") },
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .padding(top = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                LevelProgressCard(
                    currentLevel = state.currentLevelNumber,
                    hasDraft = state.hasDraft,
                    draftSize = state.draftSize,
                    draftProgress = state.draftProgress,
                    onContinue = { onNavigateToGame("level-6") },
                )

                var showRandomDialog by remember { mutableStateOf(false) }

                if (showRandomDialog) {
                    RandomPuzzleDialog(
                        onDismiss = { showRandomDialog = false },
                        onGenerate = { isColored, size, lives ->
                            onNavigateToGame("random-10")
                        }
                    )
                }

                RandomPuzzleButton(
                    onClick = { showRandomDialog = true }
                )
            }
        }
    }
}









@Composable
private fun LevelProgressCard(
    currentLevel: Int,
    hasDraft: Boolean,
    draftSize: String,
    draftProgress: Int,
    onContinue: () -> Unit,
) {
    DashboardCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20,
        borderColor = NeonCyan.copy(alpha = 0.22f),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "MEVCUT SEVİYE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        fontFamily = AppFont,
                    )
                    Text(
                        text = "$currentLevel",
                        color = NeonCyan,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont,
                        lineHeight = 36.sp,
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(NeonCyan)
                        .rippleClickable(onContinue)
                        .padding(horizontal = 18.dp, vertical = 11.dp),
                ) {
                    Text(
                        text = if (hasDraft) "Devam Et" else "Başla",
                        color = Color(0xFF0A0A0F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont,
                    )
                }
            }

            if (hasDraft) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(BorderDark),
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = PurpleAccent,
                        modifier = Modifier.size(17.dp),
                    )
                    Text(
                        text = "Yarım kalan bölüm: $draftSize · %$draftProgress",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontFamily = AppFont,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaygroundHeader(
    username: String,
    stars: Int,
    crystals: Int,
    crystalRefillMinutes: Int,
    onNavigateToSettings: () -> Unit,
    onNavigateToTutorial: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(2.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = username.take(2).uppercase(),
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = "Merhaba,",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = AppFont,
                )
                Text(
                    text = username,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(GameGold.copy(alpha = 0.13f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = GameGold,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    text = "$stars",
                    color = GameGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                )
            }

            if (crystals > 0) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(NeonCyan.copy(alpha = 0.10f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = "$crystals",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont,
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(DangerRed.copy(alpha = 0.10f))
                        .border(1.dp, DangerRed.copy(alpha = 0.30f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = DangerRed,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "${crystalRefillMinutes}dk",
                        color = DangerRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont,
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Help,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onNavigateToTutorial),
            )

            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onNavigateToSettings),
            )
        }
    }
}

@Composable
private fun WatermarkLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            BackgroundDark,
                            BackgroundDark.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        Image(
            painter = painterResource(id = R.drawable.ic_neon_cube_logo),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .alpha(0.08f)
        )
    }
}

@Composable
private fun DailyChallengeCard(
    isSolved: Boolean,
    onPlayToday: () -> Unit,
    onPlayPast: (String) -> Unit,
) {
    val locale = Locale.forLanguageTag("tr-TR")
    val today = remember {
        val calendar = Calendar.getInstance()
        SimpleDateFormat("d MMMM", locale).format(calendar.time)
    }
    val pastDates = remember {
        (1..5).map { daysAgo ->
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val label = SimpleDateFormat("d MMM", locale).format(calendar.time)
            val key = SimpleDateFormat("yyyy-MM-dd", locale).format(calendar.time)
            label to key
        }
    }

    DashboardCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20,
        borderColor = if (!isSolved) NeonCyan.copy(alpha = 0.35f) else BorderDark,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (!isSolved) NeonCyan.copy(alpha = 0.13f)
                                else SurfaceVariantDark
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = null,
                            tint = if (!isSolved) NeonCyan else TextMuted,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "GÜNÜN BULMACASI",
                            color = if (!isSolved) NeonCyan else TextMuted,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            fontFamily = AppFont,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = today,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFont,
                        )
                    }
                }

                if (!isSolved) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(NeonCyan)
                            .rippleClickable(onPlayToday)
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = "Oyna",
                            color = Color(0xFF0A0A0F),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFont,
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceVariantDark)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = "Çözüldü",
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontFamily = AppFont,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(BorderDark),
            )
            Spacer(Modifier.height(14.dp))

            Text(
                text = "GEÇMİŞ GÜNLER",
                color = TextMuted,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                pastDates.forEach { (label, key) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceVariantDark)
                            .rippleClickable { onPlayPast(key) }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = AppFont,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RandomPuzzleButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(0.5.dp, BorderDark, RoundedCornerShape(18.dp))
            .rippleClickable(onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PurpleAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = null,
                    tint = PurpleAccent,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Rastgele Bölüm Oluştur",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = AppFont,
                )
                Text(
                    text = "İnternetsiz de çalışır",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = AppFont,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SimulationToggles(
    state: PlaygroundUiState,
    onStateChange: (PlaygroundUiState) -> Unit,
) {
    DashboardCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 14,
        borderColor = GameGold.copy(alpha = 0.3f),
        backgroundColor = GameGold.copy(alpha = 0.05f),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "SİMÜLASYON",
                color = GameGold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                fontFamily = AppFont,
            )
            ToggleRow("Günlük bulmaca çözüldü", state.isDailySolved) {
                onStateChange(state.copy(isDailySolved = it))
            }
            ToggleRow("Kristal var (0 = sayaç göster)", state.crystals > 0) {
                onStateChange(state.copy(crystals = if (it) 3 else 0))
            }
            ToggleRow("Yarım kalan bölüm var", state.hasDraft) {
                onStateChange(state.copy(hasDraft = it))
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, value: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontFamily = AppFont)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (value) NeonCyan.copy(alpha = 0.25f) else SurfaceVariantDark)
                .border(
                    1.dp,
                    if (value) NeonCyan.copy(alpha = 0.6f) else BorderDark,
                    RoundedCornerShape(11.dp),
                )
                .rippleClickable { onToggle(!value) },
            contentAlignment = if (value) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (value) NeonCyan else TextMuted),
            )
        }
    }
}