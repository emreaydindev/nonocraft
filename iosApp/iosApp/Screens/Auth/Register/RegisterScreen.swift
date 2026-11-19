import Shared
import SwiftUI

struct RegisterScreen: View {
    let onNavigateToLogin: () -> Void
    let onGoogleSignIn: () -> Void
    let onRegisterSuccess: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.registerViewModel() }, state: \.state)

    @State private var username = ""
    @State private var email = ""
    @State private var password = ""
    @FocusState private var focus: Field?

    private enum Field { case username, email, password }

    private var uiState: RegisterUiState { model.state }
    private var isEmailValid: Bool { isValidEmailOrEmpty(email) }
    private var isPasswordLongEnough: Bool { password.count >= 8 }
    private var canSubmit: Bool {
        !username.isEmpty && !email.isEmpty && isEmailValid && isPasswordLongEnough && !uiState.isLoading
    }

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {

                CenteredScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        Text(L10n.string("auth_register_title"))
                            .font(.app(32, .bold))
                            .foregroundColor(.textPrimary)

                        GoogleSignInButton(label: L10n.string("auth_sign_up_with_google"), action: onGoogleSignIn)
                            .padding(.top, 32)
                        AuthDivider(label: L10n.string("auth_or_with_email"))

                        if let error = uiState.error {
                            InfoBanner(message: L10n.string("auth_register_failed"), subMessage: error.message(.register), isError: true)
                                .padding(.bottom, 14)
                        }

                        AuthTextField(
                            text: $username, label: L10n.string("auth_username"), placeholder: L10n.string("auth_username_placeholder"),
                            isError: uiState.error != nil, keyboardType: .asciiCapable, contentType: .username
                        ) {
                            if !username.isEmpty && uiState.error == nil {
                                Text("✓").font(.system(size: 15)).foregroundColor(.neonCyan)
                                    .accessibilityLabel(L10n.string("auth_valid"))
                            }
                        }
                        .focused($focus, equals: .username)
                        .onSubmit { focus = .email }
                        .onChange(of: username) { _, value in
                            username = value.trimmingCharacters(in: .whitespaces).lowercased()
                        }

                        AuthTextField(
                            text: $email, label: L10n.string("auth_email"), placeholder: L10n.string("auth_email_placeholder"),
                            isError: !isEmailValid, errorMessage: L10n.string("auth_email_invalid"),
                            keyboardType: .emailAddress, contentType: .emailAddress
                        )
                        .focused($focus, equals: .email)
                        .onSubmit { focus = .password }
                        .onChange(of: email) { _, value in
                            email = value.trimmingCharacters(in: .whitespaces)
                        }
                        .padding(.top, 12)

                        AuthPasswordField(text: $password, label: L10n.string("auth_password"), contentType: .newPassword)
                            .focused($focus, equals: .password)
                            .onSubmit(submit)
                            .padding(.top, 12)

                        if !password.isEmpty {
                            PasswordStrengthBar(password: password)
                                .padding(.top, 8)

                            VStack(alignment: .leading, spacing: 0) {
                                Text(L10n.string("auth_requirements"))
                                    .font(.app(11))
                                    .kerning(0.8)
                                    .foregroundColor(.textMuted)
                                    .padding(.bottom, 8)
                                PasswordRequirementRow(text: L10n.string("auth_req_min_length"), met: isPasswordLongEnough)
                                PasswordRequirementRow(text: L10n.string("auth_req_uppercase"), met: password.contains(where: \.isUppercase))
                                PasswordRequirementRow(text: L10n.string("auth_req_digit"), met: password.contains(where: \.isNumber))
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color.surfaceDark)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .padding(.top, 16)
                        }
                    }
                    .padding(.horizontal, 28)
                    .padding(.bottom, 24)
                }

                VStack(spacing: 0) {
                    PrimaryAuthButton(text: L10n.string("auth_sign_up"), enabled: canSubmit, isLoading: uiState.isLoading, action: submit)
                    Button(action: onNavigateToLogin) {
                        (Text(L10n.string("auth_have_account_prefix")).foregroundColor(.textSecondary)
                            + Text(L10n.string("auth_sign_in")).font(.app(13, .semibold)).foregroundColor(.neonCyan))
                            .font(.app(13))
                    }
                    .buttonStyle(.plain)
                    .padding(.top, 16)
                    LegalText()
                        .padding(.horizontal, 8)
                        .padding(.top, 20)
                }
                .padding(.horizontal, 28)
                .padding(.bottom, 24)
            }
            .readableWidth()
        }
        // System back button on a transparent bar; the heading stays in the content.
        .toolbarBackground(.hidden, for: .navigationBar)
        .onChange(of: uiState.isSuccess) { _, isSuccess in
            if isSuccess { onRegisterSuccess() }
        }
    }

    private func submit() {
        guard canSubmit else { return }
        focus = nil
        model.viewModel.register(email: email, username: username, password: password)
    }
}
