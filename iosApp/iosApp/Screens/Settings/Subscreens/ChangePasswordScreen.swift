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
        SettingsForm(title: L10n.string("settings_change_password")) {
            SettingsSection {
                SecureField(L10n.string("change_password_current"), text: $currentPassword)
                    .textContentType(.password)
            }
            SettingsSection(title: nil) {
                SecureField(L10n.string("change_password_new"), text: $newPassword)
                    .textContentType(.newPassword)
                SecureField(L10n.string("change_password_repeat"), text: $confirmPassword)
                    .textContentType(.newPassword)
            } footer: {
                if !confirmPassword.isEmpty && !passwordsMatch {
                    Label(L10n.string("auth_passwords_dont_match"), systemImage: "exclamationmark.triangle.fill")
                        .foregroundStyle(Color.dangerRed)
                } else if let errorMessage {
                    Label(errorMessage, systemImage: "exclamationmark.triangle.fill")
                        .foregroundStyle(Color.dangerRed)
                }
            }
        }
        .disabled(isLoading)
        .toolbar {
            ToolbarSaveButton(title: L10n.string("common_update"), isLoading: isLoading, isEnabled: canSubmit) {
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
