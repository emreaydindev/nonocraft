import Shared
import SwiftUI

struct LoginScreen: View {
    let onNavigateToRegister: () -> Void
    let onNavigateToForgotPassword: () -> Void
    let onGoogleSignIn: () -> Void
    let onLoginSuccess: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.loginViewModel() }, state: \.state)

    @State private var identifier = ""
    @State private var password = ""
    @FocusState private var focus: Field?

    private enum Field { case identifier, password }

    private var uiState: LoginUiState { model.state }
    private var canSubmit: Bool { !identifier.isEmpty && !password.isEmpty && !uiState.isLoading }

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {

                CenteredScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        Text(L10n.string("auth_login_title"))
                            .font(.app(32, .bold))
                            .foregroundColor(.textPrimary)
                        Text(L10n.string("auth_login_subtitle"))
                            .font(.app(15))
                            .foregroundColor(.textSecondary)
                            .padding(.top, 8)

                        GoogleSignInButton(label: L10n.string("auth_sign_in_with_google"), action: onGoogleSignIn)
                            .padding(.top, 32)
                        AuthDivider(label: L10n.string("auth_or_with_email"))

                        if let error = uiState.error {
                            InfoBanner(message: L10n.string("auth_login_failed"), subMessage: error.message(.login), isError: true)
                                .padding(.bottom, 14)
                        }

                        AuthTextField(
                            text: $identifier,
                            label: L10n.string("auth_email_or_username"),
                            placeholder: L10n.string("auth_email_or_username_placeholder"),
                            isError: uiState.error != nil,
                            keyboardType: .emailAddress,
                            contentType: .username
                        )
                        .focused($focus, equals: .identifier)
                        .onSubmit { focus = .password }
                        .onChange(of: identifier) { _, value in
                            identifier = value.trimmingCharacters(in: .whitespaces)
                        }

                        AuthPasswordField(text: $password, label: L10n.string("auth_password"), isError: uiState.error != nil)
                            .focused($focus, equals: .password)
                            .onSubmit(submit)
                            .padding(.top, 12)

                        Button(L10n.string("auth_forgot_password_link"), action: onNavigateToForgotPassword)
                            .font(.app(13))
                            .foregroundColor(.neonCyan)
                            .frame(maxWidth: .infinity, alignment: .trailing)
                            .padding(.top, 8)
                            .padding(.bottom, 24)
                    }
                    .padding(.horizontal, 28)
                }

                VStack(spacing: 24) {
                    PrimaryAuthButton(text: L10n.string("auth_sign_in"), enabled: canSubmit, isLoading: uiState.isLoading, action: submit)
                    Button(action: onNavigateToRegister) {
                        (Text(L10n.string("auth_no_account_prefix")).foregroundColor(.textSecondary)
                            + Text(L10n.string("auth_sign_up")).font(.app(13, .semibold)).foregroundColor(.neonCyan))
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
        .onChange(of: uiState.isSuccess) { _, isSuccess in
            if isSuccess { onLoginSuccess() }
        }
    }

    private func submit() {
        guard canSubmit else { return }
        focus = nil
        model.viewModel.login(email: identifier, password: password)
    }
}
