import Shared
import SwiftUI

struct SettingsScreen: View {
    let onNavigateBack: () -> Void
    let onNavigate: (SettingsRoute) -> Void
    let onLogoutSuccess: () -> Void

    @StateObject private var model = HeldViewModel { $0.settingsViewModel() }
    @Environment(\.openURL) private var openURL

    // Not persisted yet, same as on Android.
    @State private var isNeonEffectsEnabled = true
    @State private var isVibrationEnabled = true
    @State private var isSoundEnabled = true
    @State private var isErrorWarningEnabled = false

    @State private var showDeleteDialog = false
    @State private var errorMessage: String?

    var body: some View {
        SettingsScaffold(title: "Ayarlar", onBack: onNavigateBack) {
            VStack(spacing: 24) {
                SettingsGroup(title: "Görünüm & Özelleştirme") {
                    SettingsRow(icon: "paintpalette.fill", label: "Uygulama Teması", value: "Karanlık") { onNavigate(.theme) }
                    SettingsRow(icon: "app.badge.fill", label: "Uygulama İkonu", value: "Klasik", trailing: .premium) { onNavigate(.appIcon) }
                    SettingsSwitchRow(icon: "sparkles", label: "Neon Efektleri", isOn: $isNeonEffectsEnabled)
                }
                SettingsGroup(title: "Oyun Deneyimi") {
                    SettingsSwitchRow(icon: "iphone.radiowaves.left.and.right", label: "Titreşim", isOn: $isVibrationEnabled)
                    SettingsSwitchRow(icon: "speaker.wave.2.fill", label: "Ses Efektleri", isOn: $isSoundEnabled)
                    SettingsSwitchRow(icon: "exclamationmark.triangle.fill", label: "Hatalı Hamle Uyarısı", isOn: $isErrorWarningEnabled)
                }
                SettingsGroup(title: "Hesap & Güvenlik") {
                    SettingsRow(icon: "person.crop.circle.badge.checkmark", label: "Hesap Bilgilerini Düzenle") { onNavigate(.editProfile) }
                    SettingsRow(icon: "lock.fill", label: "Şifre Değiştir") { onNavigate(.changePassword) }
                    SettingsRow(icon: "bell.fill", label: "Bildirim Tercihleri") { onNavigate(.notifications) }
                }
                SettingsGroup(title: "Destek & Yasal") {
                    SettingsRow(icon: "envelope.fill", label: "Bize Ulaşın", trailing: .externalLink) {
                        open("mailto:\(Constants.shared.SUPPORT_EMAIL)")
                    }
                    SettingsRow(icon: "checkmark.shield.fill", label: "Gizlilik Politikası", trailing: .externalLink) {
                        open(Constants.shared.PRIVACY_POLICY_URL)
                    }
                    SettingsRow(icon: "doc.text.fill", label: "Kullanım Koşulları", trailing: .externalLink) {
                        open(Constants.shared.TERMS_OF_SERVICE_URL)
                    }
                }
                SettingsGroup(title: "Oturum") {
                    SettingsRow(icon: "rectangle.portrait.and.arrow.right", label: "Çıkış Yap", trailing: .none, isDestructive: true) {
                        model.viewModel.logout()
                    }
                    SettingsRow(icon: "trash.fill", label: "Hesabı Kalıcı Olarak Sil", trailing: .none, isDestructive: true) {
                        showDeleteDialog = true
                    }
                }
            }
            .padding(.bottom, 24)
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .navigateToAuth: onLogoutSuccess()
                case .showError(let error): errorMessage = error.message
                }
            }
        }
        .alert("Hesabı Sil", isPresented: $showDeleteDialog) {
            Button("İptal", role: .cancel) {}
            Button("Evet, Sil", role: .destructive) { model.viewModel.deleteAccount() }
        } message: {
            Text("Bu işlem geri alınamaz. Tüm verilerin, bulmacaların ve ilerlemen kalıcı olarak silinecek. Emin misin?")
        }
        .alert("Hata", isPresented: .init(get: { errorMessage != nil }, set: { if !$0 { errorMessage = nil } })) {
            Button("Tamam", role: .cancel) {}
        } message: {
            Text(errorMessage ?? "")
        }
    }

    private func open(_ string: String) {
        if let url = URL(string: string) { openURL(url) }
    }
}
