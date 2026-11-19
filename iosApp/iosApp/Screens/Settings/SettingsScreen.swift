import Shared
import SwiftUI

struct SettingsScreen: View {
    let onLogoutSuccess: () -> Void

    @StateObject private var model = HeldViewModel { $0.settingsViewModel() }
    @Environment(\.openURL) private var openURL

    // Not persisted yet, same as on Android.
    @State private var isNeonEffectsEnabled = true
    @State private var isVibrationEnabled = true
    @State private var isSoundEnabled = true
    @State private var isErrorWarningEnabled = false
    private let colorPatterns = ColorPatternsReader()

    @State private var showDeleteDialog = false
    @State private var errorMessage: String?

    var body: some View {
        SettingsForm(title: L10n.string("common_settings"), titleDisplayMode: .large) {
            SettingsSection(L10n.string("settings_group_appearance")) {
                NavigationLink(value: AppRoute.settingsSub(.theme)) {
                    LabeledContent {
                        Text(L10n.string("settings_theme_dark"))
                    } label: {
                        SettingsLabel(title: L10n.string("settings_theme"), systemImage: "paintpalette.fill", tint: .purpleAccent)
                    }
                }
                NavigationLink(value: AppRoute.settingsSub(.appIcon)) {
                    LabeledContent {
                        HStack(spacing: 8) {
                            Text(L10n.string("settings_icon_classic"))
                            ProBadge()
                        }
                    } label: {
                        SettingsLabel(title: L10n.string("settings_icon"), systemImage: "app.badge.fill", tint: .gameGold)
                    }
                }
                Toggle(isOn: $isNeonEffectsEnabled) {
                    SettingsLabel(title: L10n.string("settings_neon"), systemImage: "sparkles")
                }
                // iOS keeps the per-app language in the app's page in Settings; it relaunches the app on change.
                Button {
                    if let url = URL(string: UIApplication.openSettingsURLString) { openURL(url) }
                } label: {
                    LabeledContent {
                        Text(L10n.string(Bundle.main.preferredLocalizations.first == "tr" ? "settings_language_tr" : "settings_language_en"))
                    } label: {
                        SettingsLabel(title: L10n.string("settings_language"), systemImage: "globe")
                    }
                }
                .foregroundStyle(.primary)
                .accessibilityHint(L10n.string("settings_language_opens_settings"))
            }
            SettingsSection(L10n.string("settings_group_game")) {
                Toggle(isOn: $isVibrationEnabled) {
                    SettingsLabel(title: L10n.string("settings_vibration"), systemImage: "iphone.radiowaves.left.and.right")
                }
                Toggle(isOn: $isSoundEnabled) {
                    SettingsLabel(title: L10n.string("settings_sound"), systemImage: "speaker.wave.2.fill")
                }
                Toggle(isOn: $isErrorWarningEnabled) {
                    SettingsLabel(title: L10n.string("settings_wrong_move_alert"), systemImage: "exclamationmark.triangle.fill", tint: .dangerRed)
                }
                // Follows Differentiate Without Colour until the player picks; saved on the device.
                Toggle(isOn: colorPatterns.binding) {
                    SettingsLabel(title: L10n.string("settings_colorblind"), systemImage: "square.on.circle")
                }
            }
            SettingsSection(L10n.string("settings_group_account")) {
                NavigationLink(value: AppRoute.settingsSub(.editProfile)) {
                    SettingsLabel(title: L10n.string("settings_edit_account"), systemImage: "person.crop.circle.fill")
                }
                NavigationLink(value: AppRoute.settingsSub(.changePassword)) {
                    SettingsLabel(title: L10n.string("settings_change_password"), systemImage: "lock.fill")
                }
                NavigationLink(value: AppRoute.settingsSub(.notifications)) {
                    SettingsLabel(title: L10n.string("settings_notifications"), systemImage: "bell.badge.fill", tint: .dangerRed)
                }
            }
            SettingsSection(L10n.string("settings_group_support")) {
                externalLink(L10n.string("settings_contact"), systemImage: "envelope.fill", url: "mailto:\(Constants.shared.SUPPORT_EMAIL)")
                externalLink(L10n.string("common_privacy_policy"), systemImage: "checkmark.shield.fill", url: Constants.shared.PRIVACY_POLICY_URL)
                externalLink(L10n.string("common_terms"), systemImage: "doc.text.fill", url: Constants.shared.TERMS_OF_SERVICE_URL)
            }
            SettingsSection {
                Button(role: .destructive) {
                    model.viewModel.logout()
                } label: {
                    Label(L10n.string("settings_log_out"), systemImage: "rectangle.portrait.and.arrow.right")
                }
                Button(role: .destructive) {
                    showDeleteDialog = true
                } label: {
                    Label(L10n.string("settings_delete_account"), systemImage: "trash")
                }
            }
            .foregroundStyle(Color.dangerRed)
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .navigateToAuth: onLogoutSuccess()
                case .showError(let error): errorMessage = error.code.message(.deleteAccount)
                }
            }
        }
        .alert(L10n.string("delete_title"), isPresented: $showDeleteDialog) {
            Button(L10n.string("common_cancel"), role: .cancel) {}
            Button(L10n.string("delete_confirm"), role: .destructive) { model.viewModel.deleteAccount() }
        } message: {
            Text(L10n.string("delete_body"))
        }
        .alert(L10n.string("common_error"), isPresented: .init(get: { errorMessage != nil }, set: { if !$0 { errorMessage = nil } })) {
            Button(L10n.string("common_ok"), role: .cancel) {}
        } message: {
            Text(errorMessage ?? "")
        }
    }

    private func externalLink(_ title: String, systemImage: String, url: String) -> some View {
        Button {
            if let url = URL(string: url) { openURL(url) }
        } label: {
            HStack {
                SettingsLabel(title: title, systemImage: systemImage, tint: .textSecondary)
                Spacer()
                Image(systemName: "arrow.up.right")
                    .font(.footnote.weight(.semibold))
                    .foregroundStyle(.tertiary)
            }
        }
        .foregroundStyle(Color.textPrimary)
    }
}
