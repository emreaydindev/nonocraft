import Shared
import SwiftUI

struct ChangePasswordScreen: View {
    let onNavigateBack: () -> Void

    @StateObject private var model = HeldViewModel { $0.changePasswordViewModel() }
    @State private var currentPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var isLoading = false
    @State private var errorMessage: String?

    private var passwordsMatch: Bool { newPassword == confirmPassword }
    private var canSubmit: Bool { !currentPassword.isEmpty && !newPassword.isEmpty && passwordsMatch }

    var body: some View {
        SettingsScaffold(title: "Şifre Değiştir", onBack: onNavigateBack) {
            VStack(spacing: 20) {
                OutlinedField(label: "Mevcut Şifre", text: $currentPassword, isSecure: true)
                OutlinedField(label: "Yeni Şifre", text: $newPassword, isSecure: true)
                OutlinedField(label: "Yeni Şifre (Tekrar)", text: $confirmPassword, isSecure: true)

                if !confirmPassword.isEmpty && !passwordsMatch {
                    InfoBanner(message: "Şifreler eşleşmiyor", isError: true)
                } else if let errorMessage {
                    InfoBanner(message: "Şifre güncellenemedi", subMessage: errorMessage, isError: true)
                }

                SettingsActionButton(title: "Şifreyi Güncelle", isLoading: isLoading, isEnabled: canSubmit) {
                    errorMessage = nil
                    model.viewModel.changePassword(oldPassword: currentPassword, newPassword: newPassword)
                }
                .padding(.top, 24)
            }
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .loading: isLoading = true
                case .success:
                    isLoading = false
                    onNavigateBack()
                case .error(let error):
                    isLoading = false
                    errorMessage = error.message
                }
            }
        }
    }
}
