import Shared
import SwiftUI

struct ChangePasswordScreen: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var model = HeldViewModel { $0.changePasswordViewModel() }
    @State private var currentPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var isLoading = false
    @State private var errorMessage: String?

    private var passwordsMatch: Bool { newPassword == confirmPassword }
    private var canSubmit: Bool { !currentPassword.isEmpty && !newPassword.isEmpty && passwordsMatch }

    var body: some View {
        SettingsForm(title: "Şifre Değiştir") {
            SettingsSection {
                SecureField("Mevcut Şifre", text: $currentPassword)
                    .textContentType(.password)
            }
            SettingsSection(title: nil) {
                SecureField("Yeni Şifre", text: $newPassword)
                    .textContentType(.newPassword)
                SecureField("Yeni Şifre (Tekrar)", text: $confirmPassword)
                    .textContentType(.newPassword)
            } footer: {
                if !confirmPassword.isEmpty && !passwordsMatch {
                    Label("Şifreler eşleşmiyor", systemImage: "exclamationmark.triangle.fill")
                        .foregroundStyle(Color.dangerRed)
                } else if let errorMessage {
                    Label(errorMessage, systemImage: "exclamationmark.triangle.fill")
                        .foregroundStyle(Color.dangerRed)
                }
            }
        }
        .disabled(isLoading)
        .toolbar {
            ToolbarSaveButton(title: "Güncelle", isLoading: isLoading, isEnabled: canSubmit) {
                errorMessage = nil
                model.viewModel.changePassword(oldPassword: currentPassword, newPassword: newPassword)
            }
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .loading: isLoading = true
                case .success:
                    isLoading = false
                    dismiss()
                case .error(let error):
                    isLoading = false
                    errorMessage = error.code.message(.changePassword)
                }
            }
        }
    }
}
