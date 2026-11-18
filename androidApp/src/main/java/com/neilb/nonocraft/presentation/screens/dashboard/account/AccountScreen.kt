package com.neilb.nonocraft.presentation.screens.dashboard.account

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.neilb.nonocraft.presentation.screens.auth.components.PrimaryAuthButton
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardCard
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.GameGold
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary

@Composable
fun AccountScreen(
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCrafting: () -> Unit,
    hasGoldHighlight: Boolean = true,
    onNavigateToAuthGateway: () -> Unit,
    viewModel: AccountViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
    ) {
        GridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 16.dp, bottom = 24.dp),
        ) {
            Text(
                text = "Profilim",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 16.dp)
                    .semantics { heading() }
            )

            DashboardCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24
            ) {
                ProfileHeader(
                    uiState = uiState,
                    onNavigateToAuthGateway = onNavigateToAuthGateway,
                    onNavigateToCrafting = onNavigateToCrafting
                )
            }

            Spacer(Modifier.height(24.dp))

            MenuRow(
                icon = Icons.Rounded.WorkspacePremium,
                iconColor = GameGold,
                label = "Premium'a Geç",
                onClick = onNavigateToPremium,
            )
            Spacer(Modifier.height(8.dp))
            MenuRow(
                icon = Icons.Rounded.Settings,
                iconColor = TextSecondary,
                label = "Ayarlar",
                onClick = onNavigateToSettings,
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    uiState: AccountUiState,
    onNavigateToCrafting: () -> Unit,
    onNavigateToAuthGateway: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(SurfaceVariantDark)
                .rippleClickable(onNavigateToCrafting)
                .clearAndSetSemantics { contentDescription = "Avatarı düzenle" },
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.avatarUrl != null) {
                // AsyncImage(model = uiState.avatarUrl, ...) -- coil eklenmeli TODO
            } else {
                Text(
                    text = if (uiState.isGuest) "?" else uiState.username.getOrNull(1)?.uppercase() ?: "N",
                    color = NeonCyan,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (uiState.isGuest) {
            Text("Misafir Kullanıcı", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
            Spacer(Modifier.height(4.dp))
            Text("İlerlemeni kaybetmemek için giriş yap", color = TextMuted, fontSize = 13.sp, fontFamily = AppFont)
            Spacer(Modifier.height(16.dp))
            PrimaryAuthButton(text = "Giriş Yap / Kayıt Ol", onClick = onNavigateToAuthGateway)
        } else {
            Text(uiState.username, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
            Spacer(Modifier.height(4.dp))
            Text(uiState.bio, color = TextMuted, fontSize = 14.sp, fontFamily = AppFont)
            Spacer(Modifier.height(20.dp))
            // İstatistik satırı -> gerçek veri gelene kadar TODO
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = AppFont)
        Text(label, color = TextMuted, fontSize = 12.sp, fontFamily = AppFont)
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    onClick: () -> Unit,
    backgroundColor: Color = SurfaceDark,
    borderColor: Color = BorderDark,
    textColor: Color = TextPrimary,
) {
    DashboardCard(
        modifier = Modifier
            .fillMaxWidth()
            .rippleClickable(onClick),
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        cornerRadius = 14,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(label, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = AppFont)
            }
            Text("›", color = TextMuted, fontSize = 20.sp, modifier = Modifier.clearAndSetSemantics { })
        }
    }
}