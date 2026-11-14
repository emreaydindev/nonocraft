import Shared
import SwiftUI

enum AuthRoute: Hashable {
    case login, register
}

struct RootView: View {
    private enum Phase {
        case splash, auth, dashboard
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
            case .auth:
                authStack
            case .dashboard:
                DashboardScreen()
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
                    store.owner.appViewModel().enterGuestMode()
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

    // Onboarding and the tutorial are not ported to iOS yet, so those destinations continue to the
    // screen that follows them on Android. Onboarding is marked as seen, otherwise the splash
    // would keep returning it and a signed-in user would never reach the dashboard.
    private func route(_ destination: SplashDestination) {
        switch onEnum(of: destination) {
        case .dashboard, .tutorial:
            phase = .dashboard
        case .onboarding:
            store.owner.appViewModel().completeOnboarding()
            phase = .auth
        case .authGateway, .loading:
            phase = .auth
        }
    }

    private func enterDashboard() {
        authPath = []
        phase = .dashboard
    }
}
