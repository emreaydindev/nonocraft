package com.neilb.nonocraft.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.AppShortcut
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.neilb.nonocraft.common.showToast
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardCard
import com.neilb.nonocraft.presentation.screens.dashboard.components.SectionLabel
import com.neilb.nonocraft.presentation.screens.dashboard.components.noRippleClickable
import com.neilb.nonocraft.presentation.screens.settings.components.DeleteAccountDialog
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.GameGold
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToTheme: () -> Unit,
    onNavigateToAppIcon: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current

    var isNeonEffectsEnabled by remember { mutableStateOf(true) }
    var isVibrationEnabled by remember { mutableStateOf(true) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var isErrorWarningEnabled by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingsUiEvent.NavigateToAuth -> onLogoutSuccess()
                is SettingsUiEvent.ShowError -> context.showToast(event.message)
            }
        }
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteAccount()
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .navigationBarsPadding()
    ) {
        GridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Geri",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "Ayarlar",
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
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                SettingsGroup(title = "Görünüm & Özelleştirme") {
                    SettingsRow(
                        icon = Icons.Rounded.Palette,
                        label = "Uygulama Teması",
                        value = "Karanlık",
                        onClick = onNavigateToTheme
                    )
                    SettingsRow(
                        icon = Icons.Rounded.AppShortcut,
                        label = "Uygulama İkonu",
                        value = "Klasik",
                        isPremium = true,
                        onClick = onNavigateToAppIcon
                    )
                    SettingsSwitchRow(
                        icon = Icons.Rounded.AutoAwesome,
                        label = "Neon Efektleri",
                        isChecked = isNeonEffectsEnabled,
                        onCheckedChange = { isNeonEffectsEnabled = it }
                    )
                }

                SettingsGroup(title = "Oyun Deneyimi") {
                    SettingsSwitchRow(
                        icon = Icons.Rounded.Vibration,
                        label = "Titreşim",
                        isChecked = isVibrationEnabled,
                        onCheckedChange = { isVibrationEnabled = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        label = "Ses Efektleri",
                        isChecked = isSoundEnabled,
                        onCheckedChange = { isSoundEnabled = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Rounded.Warning,
                        label = "Hatalı Hamle Uyarısı",
                        isChecked = isErrorWarningEnabled,
                        onCheckedChange = { isErrorWarningEnabled = it }
                    )
                }

                SettingsGroup(title = "Hesap & Güvenlik") {
                    SettingsRow(
                        icon = Icons.Rounded.ManageAccounts,
                        label = "Hesap Bilgilerini Düzenle",
                        onClick = onNavigateToEditProfile
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Lock,
                        label = "Şifre Değiştir",
                        onClick = onNavigateToChangePassword
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Notifications,
                        label = "Bildirim Tercihleri",
                        onClick = onNavigateToNotifications
                    )
                }

                SettingsGroup(title = "Destek & Yasal") {
                    SettingsRow(
                        icon = Icons.Rounded.Email,
                        label = "Bize Ulaşın",
                        isExternalLink = true,
                        onClick = { }
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Policy,
                        label = "Gizlilik Politikası",
                        isExternalLink = true,
                        onClick = { }
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Description,
                        label = "Kullanım Koşulları",
                        isExternalLink = true,
                        onClick = { }
                    )
                }

                SettingsGroup(title = "Oturum") {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        label = "Çıkış Yap",
                        contentColor = DangerRed,
                        hideTrailingIcon = true,
                        onClick = { viewModel.logout() }
                    )
                    SettingsRow(
                        icon = Icons.Rounded.DeleteForever,
                        label = "Hesabı Kalıcı Olarak Sil",
                        contentColor = DangerRed,
                        hideTrailingIcon = true,
                        onClick = { showDeleteDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        SectionLabel(
            text = title,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        DashboardCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20,
            backgroundColor = SurfaceDark,
            borderColor = BorderDark
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    value: String? = null,
    isPremium: Boolean = false,
    isExternalLink: Boolean = false,
    contentColor: Color = TextPrimary,
    hideTrailingIcon: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (contentColor == DangerRed) DangerRed else TextMuted,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AppFont
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (value != null) {
                Text(
                    text = value,
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontFamily = AppFont
                )
            }

            if (isPremium) {
                Icon(
                    imageVector = Icons.Rounded.WorkspacePremium,
                    contentDescription = "Premium",
                    tint = GameGold,
                    modifier = Modifier.size(18.dp)
                )
            } else if (!hideTrailingIcon) {
                Icon(
                    imageVector = if (isExternalLink) Icons.AutoMirrored.Rounded.OpenInNew else Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextMuted.copy(alpha = 0.5f),
                    modifier = Modifier.size(if (isExternalLink) 18.dp else 14.dp)
                )
            }
        }
    }
    HorizontalDivider(color = BorderDark, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AppFont
            )
        }

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
    HorizontalDivider(color = BorderDark, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
}