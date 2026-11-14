import SwiftUI

struct DashboardScreen: View {
    let onNavigateToAuthGateway: () -> Void

    @State private var currentTab: DashboardTab = .playground
    @State private var path: [AppRoute] = []

    var body: some View {
        NavigationStack(path: $path) {
            VStack(spacing: 0) {
                Group {
                    switch currentTab {
                    case .playground:
                        PlaygroundScreen(
                            onNavigateToGame: { path.append(.game(id: $0)) },
                            onNavigateToSettings: { path.append(.settings) },
                            onNavigateToTutorial: { path.append(.tutorial) }
                        )
                    case .feed:
                        NotPortedYetView(title: currentTab.label)
                    case .library:
                        LibraryScreen(
                            onNavigateToCrafting: { path.append(.crafting) },
                            onNavigateToPremium: { path.append(.premium) },
                            onNavigateToCategory: { path.append(.libraryDetail(id: $0)) },
                            onNavigateToPlaylist: { path.append(.libraryDetail(id: $0)) }
                        )
                    case .account:
                        AccountScreen(
                            onNavigateToPremium: { path.append(.premium) },
                            onNavigateToSettings: { path.append(.settings) },
                            onNavigateToCrafting: { path.append(.crafting) },
                            onNavigateToAuthGateway: onNavigateToAuthGateway
                        )
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)

                DashboardBottomNavBar(currentTab: $currentTab)
            }
            .background(Color.backgroundDark.ignoresSafeArea())
            .toolbar(.hidden, for: .navigationBar)
            .navigationDestination(for: AppRoute.self, destination: destination)
        }
    }

    @ViewBuilder
    private func destination(_ route: AppRoute) -> some View {
        let back = { _ = path.popLast() }
        switch route {
        case .settings:
            SettingsScreen(
                onNavigateBack: back,
                onNavigate: { path.append(.settingsSub($0)) },
                onLogoutSuccess: onNavigateToAuthGateway
            )
        case .settingsSub(.theme): ThemeScreen(onNavigateBack: back)
        case .settingsSub(.appIcon): AppIconScreen(onNavigateBack: back)
        case .settingsSub(.editProfile): EditProfileScreen(onNavigateBack: back)
        case .settingsSub(.changePassword): ChangePasswordScreen(onNavigateBack: back)
        case .settingsSub(.notifications): NotificationSettingsScreen(onNavigateBack: back)
        case .game(let id): GameScreen(gameId: id, onNavigateBack: back)
        case .tutorial: TutorialScreen(onNavigateToDashboard: back)
        case .premium, .crafting, .libraryDetail, .profile:
            NotPortedYetView(title: "Yakında", onBack: back)
        }
    }
}

/// Stand-in for screens whose SwiftUI port has not been written yet.
struct NotPortedYetView: View {
    let title: String
    var onBack: (() -> Void)?

    var body: some View {
        ScreenBackground {
            VStack(spacing: 8) {
                Text(title)
                    .font(.app(22, .bold))
                    .foregroundColor(.textPrimary)
                Text("Bu ekran iOS sürümüne taşınıyor.")
                    .font(.app(13))
                    .foregroundColor(.textSecondary)
            }
        }
        .overlay(alignment: .topLeading) {
            if let onBack {
                AuthBackRow(onBack: onBack)
            }
        }
        .toolbar(.hidden, for: .navigationBar)
    }
}
