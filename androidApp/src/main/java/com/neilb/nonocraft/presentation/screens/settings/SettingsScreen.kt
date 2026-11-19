package com.neilb.nonocraft.presentation.screens.settings

import com.neilb.nonocraft.presentation.screens.gameplay.components.rememberColorPatternSetting
import com.neilb.nonocraft.presentation.screens.settings.components.LanguageDialog
import com.neilb.nonocraft.presentation.screens.settings.components.AppLanguage
import androidx.compose.material.icons.rounded.Language
import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import com.neilb.nonocraft.presentation.text.ErrorContext
import com.neilb.nonocraft.presentation.text.toMessage
import androidx.compose.material.icons.rounded.Category
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
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
import com.neilb.nonocraft.presentation.adaptive.isLargeFontScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.neilb.nonocraft.common.Constants
import com.neilb.nonocraft.common.showToast
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardCard
import com.neilb.nonocraft.presentation.screens.dashboard.components.SectionLabel
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
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
import androidx.core.net.toUri
import com.neilb.nonocraft.common.openInCustomTab

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
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
    var showLanguageDialog by remember { mutableStateOf(false) }
    val currentLanguage = remember { AppLanguage.current() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingsUiEvent.NavigateToAuth -> onLogoutSuccess()
                is SettingsUiEvent.ShowError -> context.showToast(event.code.toMessage(context, ErrorContext.DELETE_ACCOUNT))
            }
        }
    }

    if (showLanguageDialog) {
        LanguageDialog(
            selected = currentLanguage,
            onSelect = { language ->
                showLanguageDialog = false
                if (language != currentLanguage) AppLanguage.apply(language)
            },
            onDismiss = { showLanguageDialog = false },
        )
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
                        contentDescription = stringResource(R.string.common_back),
                        tint = TextPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.common_settings),
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
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                val colorPatterns = rememberColorPatternSetting()
                SettingsGroup(title = stringResource(R.string.settings_group_appearance)) {
                    SettingsRow(
                        icon = Icons.Rounded.Palette,
                        label = stringResource(R.string.settings_theme),
                        value = stringResource(R.string.settings_theme_dark),
                        onClick = onNavigateToTheme
                    )
                    SettingsRow(
                        icon = Icons.Rounded.AppShortcut,
                        label = stringResource(R.string.settings_icon),
                        value = stringResource(R.string.settings_icon_classic),
                        isPremium = true,
                        onClick = onNavigateToAppIcon
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Language,
                        label = stringResource(R.string.settings_language),
                        value = stringResource(currentLanguage.label),
                        onClick = { showLanguageDialog = true }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Rounded.AutoAwesome,
                        label = stringResource(R.string.settings_neon),
                        isChecked = isNeonEffectsEnabled,
                        onCheckedChange = { isNeonEffectsEnabled = it }
                    )
                }

                SettingsGroup(title = stringResource(R.string.settings_group_game)) {
                    SettingsSwitchRow(
                        icon = Icons.Rounded.Vibration,
                        label = stringResource(R.string.settings_vibration),
                        isChecked = isVibrationEnabled,
                        onCheckedChange = { isVibrationEnabled = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.AutoMirrored.Rounded.VolumeUp,
                        label = stringResource(R.string.settings_sound),
                        isChecked = isSoundEnabled,
                        onCheckedChange = { isSoundEnabled = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Rounded.Warning,
                        label = stringResource(R.string.settings_wrong_move_alert),
                        isChecked = isErrorWarningEnabled,
                        onCheckedChange = { isErrorWarningEnabled = it }
                    )
                    // Off by default unless the system colour correction is on; saved on the device.
                    SettingsSwitchRow(
                        icon = Icons.Rounded.Category,
                        label = stringResource(R.string.settings_colorblind),
                        isChecked = colorPatterns.isEnabled,
                        onCheckedChange = { colorPatterns.isEnabled = it }
                    )
                }

                SettingsGroup(title = stringResource(R.string.settings_group_account)) {
                    SettingsRow(
                        icon = Icons.Rounded.ManageAccounts,
                        label = stringResource(R.string.settings_edit_account),
                        onClick = onNavigateToEditProfile
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Lock,
                        label = stringResource(R.string.settings_change_password),
                        onClick = onNavigateToChangePassword
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Notifications,
                        label = stringResource(R.string.settings_notifications),
                        onClick = onNavigateToNotifications
                    )
                }

                SettingsGroup(title = stringResource(R.string.settings_group_support)) {
                    SettingsRow(
                        icon = Icons.Rounded.Email,
                        label = stringResource(R.string.settings_contact),
                        isExternalLink = true,
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                data = "mailto:${Constants.SUPPORT_EMAIL}".toUri()
                            }
                            context.startActivity(intent)
                        }
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Policy,
                        label = stringResource(R.string.common_privacy_policy),
                        isExternalLink = true,
                        onClick = { context.openInCustomTab(Constants.PRIVACY_POLICY_URL) }
                    )
                    SettingsRow(
                        icon = Icons.Rounded.Description,
                        label = stringResource(R.string.common_terms),
                        isExternalLink = true,
                        onClick = { context.openInCustomTab(Constants.TERMS_OF_SERVICE_URL) }
                    )
                }

                SettingsGroup(title = stringResource(R.string.settings_group_session)) {
                    SettingsRow(
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        label = stringResource(R.string.settings_log_out),
                        contentColor = DangerRed,
                        hideTrailingIcon = true,
                        onClick = { viewModel.logout() }
                    )
                    SettingsRow(
                        icon = Icons.Rounded.DeleteForever,
                        label = stringResource(R.string.settings_delete_account),
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
            modifier = Modifier
                .padding(start = 4.dp, bottom = 8.dp)
                .semantics { heading() }
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
            .rippleClickable(onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // The label takes the free width and wraps; at the largest font scales the value moves under it.
        val valueBelow = value != null && isLargeFontScale()
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (contentColor == DangerRed) DangerRed else TextMuted,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = AppFont
                )
                if (valueBelow) {
                    Text(
                        text = value.orEmpty(),
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontFamily = AppFont
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (value != null && !valueBelow) {
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
                    contentDescription = stringResource(R.string.common_premium),
                    tint = GameGold,
                    modifier = Modifier.size(18.dp)
                )
            } else if (!hideTrailingIcon) {
                Icon(
                    imageVector = if (isExternalLink) Icons.AutoMirrored.Rounded.OpenInNew else Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = if (isExternalLink) stringResource(R.string.common_opens_in_browser) else null,
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
    // The whole row is the switch, so TalkBack reads the label with the state in one stop.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = isChecked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f).padding(end = 12.dp),
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
                fontFamily = AppFont,
                modifier = Modifier.weight(1f)
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = null,
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