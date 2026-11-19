import Shared
import SwiftUI

struct RootView: View {
    private enum Phase {
        case splash, onboarding, auth, tutorial, dashboard
    }

    @StateObject private var store = ViewModelStore()
    @State private var phase: Phase = .splash
    @State private var authPath: [AuthRoute] = []
    @State private var showGoogleUnavailable = false
    @State private var showPasswordUpdated = false

    var body: some View {
        Group {
            switch phase {
            case .splash:
                SplashScreen(onFinished: route)
            case .onboarding:
                OnboardingScreen {
                    appViewModel.completeOnboarding()
                    phase = .auth
                }
            case .auth:
                authStack
            case .tutorial:
                TutorialScreen {
                    appViewModel.completeTutorial()
                    phase = .dashboard
                }
            case .dashboard:
                DashboardScreen(onNavigateToAuthGateway: {
                    authPath = []
                    phase = .auth
                })
            }
        }
        .animation(.easeInOut(duration: 0.25), value: phase)
        .alert(L10n.string("auth_password_updated"), isPresented: $showPasswordUpdated) {
            Button(L10n.string("common_ok"), role: .cancel) {}
        }
        .alert(L10n.string("auth_google_title"), isPresented: $showGoogleUnavailable) {
            Button(L10n.string("common_ok"), role: .cancel) {}
        } message: {
            Text(L10n.string("auth_google_unavailable"))
        }
    }

    private var authStack: some View {
        NavigationStack(path: $authPath) {
            AuthGatewayScreen(
                onNavigateToLogin: { authPath = [.login] },
                onNavigateToRegister: { authPath = [.register] },
                onContinueAsGuest: {
                    appViewModel.enterGuestMode()
                    enterDashboard()
                },
                onGoogleSignIn: { showGoogleUnavailable = true }
            )
            .navigationDestination(for: AuthRoute.self) { route in
                authDestination(route)
                    // The gateway has no title, so iOS 18 would label the back button "Back". Chevron only,
                    // like the iOS 26 glass back button.
                    .toolbarRole(.editor)
            }
        }
        // Same accent as the dashboard (back buttons, links); otherwise iOS 18 falls back to system blue.
        .tint(.purpleAccent)
    }

    @ViewBuilder
    private func authDestination(_ route: AuthRoute) -> some View {
        switch route {
        case .login:
            LoginScreen(
                onNavigateToRegister: { authPath = [.register] },
                onNavigateToForgotPassword: { authPath.append(.forgotPassword) },
                onGoogleSignIn: { showGoogleUnavailable = true },
                onLoginSuccess: enterDashboard
            )
        case .forgotPassword:
            ForgotPasswordScreen(
                onNavigateBack: { _ = authPath.popLast() },
                onCodeSentSuccess: { authPath.append(.resetPassword) }
            )
        case .resetPassword:
            ResetPasswordScreen(
                onResetSuccess: {
                    authPath = [.login]
                    showPasswordUpdated = true
                }
            )
        case .register:
            RegisterScreen(
                onNavigateToLogin: { authPath = [.login] },
                onGoogleSignIn: { showGoogleUnavailable = true },
                onRegisterSuccess: enterDashboard
            )
        }
    }

    private var appViewModel: AppViewModel { store.owner.appViewModel() }

    private func route(_ destination: SplashDestination) {
        switch onEnum(of: destination) {
        case .onboarding: phase = .onboarding
        case .tutorial: phase = .tutorial
        case .dashboard: phase = .dashboard
        case .authGateway, .loading: phase = .auth
        }
    }

    /// After signing in or continuing as a guest: first-time users see the tutorial.
    private func enterDashboard() {
        authPath = []
        phase = appViewModel.hasSeenTutorial ? .dashboard : .tutorial
    }
}
