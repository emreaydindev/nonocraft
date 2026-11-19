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
                        Text(L10n.string("auth_forgot_title"))
                            .font(.app(32, .bold))
                            .foregroundColor(.textPrimary)
                        Text(L10n.string("auth_forgot_subtitle"))
                            .font(.app(15))
                            .lineSpacing(4)
                            .foregroundColor(.textSecondary)
                            .padding(.top, 8)

                        AuthTextField(
                            text: $email, label: L10n.string("auth_email_address"), placeholder: L10n.string("auth_email_placeholder"),
                            isError: !isEmailValid || uiState.error != nil,
                            errorMessage: isEmailValid ? uiState.error?.message(.forgotPassword) : L10n.string("auth_email_invalid"),
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
                    PrimaryAuthButton(text: L10n.string("auth_send_code"), enabled: canSubmit, isLoading: uiState.isLoading, action: submit)
                    Button(action: onNavigateBack) {
                        (Text(L10n.string("auth_back_to_login_prefix")).foregroundColor(.textSecondary)
                            + Text(L10n.string("auth_back_to_login_link")).font(.app(13, .semibold)).foregroundColor(.neonCyan))
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
