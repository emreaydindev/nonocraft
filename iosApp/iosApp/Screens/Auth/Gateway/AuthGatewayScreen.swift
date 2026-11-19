import SwiftUI

struct AuthGatewayScreen: View {
    let onNavigateToLogin: () -> Void
    let onNavigateToRegister: () -> Void
    let onContinueAsGuest: () -> Void
    let onGoogleSignIn: () -> Void

    var body: some View {
        ScreenBackground {
            // A fixed page; scrolls at accessibility text sizes.
            LargeTextScrollView {
                VStack(spacing: 0) {
                    VStack(alignment: .leading, spacing: 0) {
                        AuthLogoRow()
                            .padding(.bottom, 32)
                        Text(L10n.string("auth_gateway_title"))
                            .font(.app(32, .bold))
                            .foregroundColor(.textPrimary)
                        Text(L10n.string("auth_gateway_subtitle"))
                            .font(.app(15))
                            .foregroundColor(.textSecondary)
                            .padding(.top, 8)
                    }
                    .frame(maxWidth: .infinity, minHeight: 200, maxHeight: .infinity, alignment: .leading)

                    VStack(spacing: 0) {
                        GoogleSignInButton(label: L10n.string("auth_continue_with_google"), action: onGoogleSignIn)
                        AuthDivider(label: L10n.string("auth_or"))
                        PrimaryAuthButton(text: L10n.string("auth_sign_up"), action: onNavigateToRegister)
                        OutlineAuthButton(text: L10n.string("auth_have_account"), action: onNavigateToLogin)
                            .padding(.top, 12)
                        GhostAuthButton(text: L10n.string("auth_continue_as_guest"), action: onContinueAsGuest)
                            .padding(.top, 12)
                        LegalText()
                            .padding(.horizontal, 16)
                            .padding(.top, 24)
                    }
                }
                .padding(.horizontal, 28)
                .padding(.bottom, 24)
            }
            .readableWidth()
        }
        .toolbar(.hidden, for: .navigationBar)
    }
}
