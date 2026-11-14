import SwiftUI

struct AuthGatewayScreen: View {
    let onNavigateToLogin: () -> Void
    let onNavigateToRegister: () -> Void
    let onContinueAsGuest: () -> Void
    let onGoogleSignIn: () -> Void

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {
                VStack(alignment: .leading, spacing: 0) {
                    AuthLogoRow()
                        .padding(.bottom, 32)
                    Text("Hemen Başla")
                        .font(.app(32, .bold))
                        .foregroundColor(.textPrimary)
                    Text("Topluluğa katıl, tasarla ve keşfet.")
                        .font(.app(15))
                        .foregroundColor(.textSecondary)
                        .padding(.top, 8)
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)

                VStack(spacing: 0) {
                    GoogleSignInButton(label: "Google ile devam et", action: onGoogleSignIn)
                    AuthDivider(label: "veya")
                    PrimaryAuthButton(text: "Kayıt Ol", action: onNavigateToRegister)
                    OutlineAuthButton(text: "Zaten hesabım var", action: onNavigateToLogin)
                        .padding(.top, 12)
                    GhostAuthButton(text: "Misafir olarak devam et", action: onContinueAsGuest)
                        .padding(.top, 12)
                    LegalText()
                        .padding(.horizontal, 16)
                        .padding(.top, 24)
                }
            }
            .padding(.horizontal, 28)
            .padding(.bottom, 24)
        }
        .toolbar(.hidden, for: .navigationBar)
    }
}
