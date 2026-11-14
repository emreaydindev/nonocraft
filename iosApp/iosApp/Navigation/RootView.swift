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
        .alert("Google ile giriş", isPresented: $showGoogleUnavailable) {
            Button("Tamam", role: .cancel) {}
        } message: {
            Text("Google ile giriş iOS sürümünde henüz etkin değil. E-posta ile devam edebilirsin.")
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
                switch route {
                case .login:
                    LoginScreen(
                        onNavigateBack: { authPath = [] },
                        onNavigateToRegister: { authPath = [.register] },
                        onNavigateToForgotPassword: {},
                        onGoogleSignIn: { showGoogleUnavailable = true },
                        onLoginSuccess: enterDashboard
                    )
                case .register:
                    RegisterScreen(
                        onNavigateBack: { authPath = [] },
                        onNavigateToLogin: { authPath = [.login] },
                        onGoogleSignIn: { showGoogleUnavailable = true },
                        onRegisterSuccess: enterDashboard
                    )
                }
            }
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
