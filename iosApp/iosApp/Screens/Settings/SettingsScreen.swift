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
        SettingsForm(title: "Ayarlar", titleDisplayMode: .large) {
            SettingsSection("Görünüm & Özelleştirme") {
                NavigationLink(value: AppRoute.settingsSub(.theme)) {
                    LabeledContent {
                        Text("Karanlık")
                    } label: {
                        SettingsLabel(title: "Uygulama Teması", systemImage: "paintpalette.fill", tint: .purpleAccent)
                    }
                }
                NavigationLink(value: AppRoute.settingsSub(.appIcon)) {
                    LabeledContent {
                        HStack(spacing: 8) {
                            Text("Klasik")
                            ProBadge()
                        }
                    } label: {
                        SettingsLabel(title: "Uygulama İkonu", systemImage: "app.badge.fill", tint: .gameGold)
                    }
                }
                Toggle(isOn: $isNeonEffectsEnabled) {
                    SettingsLabel(title: "Neon Efektleri", systemImage: "sparkles")
                }
            }
            SettingsSection("Oyun Deneyimi") {
                Toggle(isOn: $isVibrationEnabled) {
                    SettingsLabel(title: "Titreşim", systemImage: "iphone.radiowaves.left.and.right")
                }
                Toggle(isOn: $isSoundEnabled) {
                    SettingsLabel(title: "Ses Efektleri", systemImage: "speaker.wave.2.fill")
                }
                Toggle(isOn: $isErrorWarningEnabled) {
                    SettingsLabel(title: "Hatalı Hamle Uyarısı", systemImage: "exclamationmark.triangle.fill", tint: .dangerRed)
                }
                // Follows Differentiate Without Colour until the player picks; saved on the device.
                Toggle(isOn: colorPatterns.binding) {
                    SettingsLabel(title: "Renk Körü Desenleri", systemImage: "square.on.circle")
                }
            }
            SettingsSection("Hesap & Güvenlik") {
                NavigationLink(value: AppRoute.settingsSub(.editProfile)) {
                    SettingsLabel(title: "Hesap Bilgilerini Düzenle", systemImage: "person.crop.circle.fill")
                }
                NavigationLink(value: AppRoute.settingsSub(.changePassword)) {
                    SettingsLabel(title: "Şifre Değiştir", systemImage: "lock.fill")
                }
                NavigationLink(value: AppRoute.settingsSub(.notifications)) {
                    SettingsLabel(title: "Bildirim Tercihleri", systemImage: "bell.badge.fill", tint: .dangerRed)
                }
            }
            SettingsSection("Destek & Yasal") {
                externalLink("Bize Ulaşın", systemImage: "envelope.fill", url: "mailto:\(Constants.shared.SUPPORT_EMAIL)")
                externalLink("Gizlilik Politikası", systemImage: "checkmark.shield.fill", url: Constants.shared.PRIVACY_POLICY_URL)
                externalLink("Kullanım Koşulları", systemImage: "doc.text.fill", url: Constants.shared.TERMS_OF_SERVICE_URL)
            }
            SettingsSection {
                Button(role: .destructive) {
                    model.viewModel.logout()
                } label: {
                    Label("Çıkış Yap", systemImage: "rectangle.portrait.and.arrow.right")
                }
                Button(role: .destructive) {
                    showDeleteDialog = true
                } label: {
                    Label("Hesabı Kalıcı Olarak Sil", systemImage: "trash")
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
