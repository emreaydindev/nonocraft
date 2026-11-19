import Shared
import SwiftUI

/// Shared with RegisterScreen: empty input is treated as valid so the error only shows once typing starts.
func isValidEmailOrEmpty(_ email: String) -> Bool {
    email.isEmpty || email.wholeMatch(of: /[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}/) != nil
}

struct ForgotPasswordScreen: View {
    let onNavigateBack: () -> Void
    let onCodeSentSuccess: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.forgotPasswordViewModel() }, state: \.state)
    @State private var email = ""

    private var uiState: ForgotPasswordUiState { model.state }
    private var isEmailValid: Bool { isValidEmailOrEmpty(email) }
    private var canSubmit: Bool { !email.isEmpty && isEmailValid && !uiState.isLoading }

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {

                CenteredScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Şifremi Unuttum")
                            .font(.app(32, .bold))
                            .foregroundColor(.textPrimary)
                        Text("E-posta adresini gir, sana bir doğrulama kodu gönderelim.")
                            .font(.app(15))
                            .lineSpacing(4)
                            .foregroundColor(.textSecondary)
                            .padding(.top, 8)

                        AuthTextField(
                            text: $email, label: "E-posta adresi", placeholder: "ornek@gmail.com",
                            isError: !isEmailValid || uiState.error != nil,
                            errorMessage: isEmailValid ? uiState.error?.message(.forgotPassword) : "Geçerli bir e-posta adresi girin",
                            keyboardType: .emailAddress, contentType: .emailAddress, submitLabel: .done
                        )
                        .onSubmit(submit)
                        .onChange(of: email) { _, value in
                            email = value.trimmingCharacters(in: .whitespaces)
                            if uiState.error != nil { model.viewModel.clearError() }
                        }
                        .padding(.top, 32)
                    }
                    .padding(.horizontal, 28)
                }

                VStack(spacing: 24) {
                    PrimaryAuthButton(text: "Kod Gönder", enabled: canSubmit, isLoading: uiState.isLoading, action: submit)
                    Button(action: onNavigateBack) {
                        (Text("Giriş sayfasına ").foregroundColor(.textSecondary)
                            + Text("dön").font(.app(13, .semibold)).foregroundColor(.neonCyan))
                            .font(.app(13))
                    }
                    .buttonStyle(.plain)
                }
                .padding(.horizontal, 28)
                .padding(.bottom, 24)
            }
            .readableWidth()
        }
        // System back button on a transparent bar; the heading stays in the content.
        .toolbarBackground(.hidden, for: .navigationBar)
        .onChange(of: uiState.isSent) { _, isSent in
            if isSent { onCodeSentSuccess() }
        }
    }

    private func submit() {
        guard canSubmit else { return }
        model.viewModel.sendResetCode(email: email)
    }
}
