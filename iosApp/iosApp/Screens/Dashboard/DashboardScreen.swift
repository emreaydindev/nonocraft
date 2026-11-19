import SwiftUI

struct DashboardScreen: View {
    let onNavigateToAuthGateway: () -> Void

    @State private var currentTab: DashboardTab = .playground
    /// Each tab keeps its own navigation history, like every system app with a tab bar.
    @State private var paths: [DashboardTab: [AppRoute]] = [:]
    @State private var isPremiumPresented = false

    var body: some View {
        TabView(selection: $currentTab) {
            Tab(DashboardTab.playground.label, systemImage: DashboardTab.playground.systemImage, value: .playground) {
                stack(.playground) { push in
                    PlaygroundScreen(
                        onNavigateToGame: { push(.game(id: $0)) },
                        onNavigateToSettings: { push(.settings) },
                        onNavigateToTutorial: { push(.tutorial) }
                    )
                }
            }
            Tab(DashboardTab.feed.label, systemImage: DashboardTab.feed.systemImage, value: .feed) {
                stack(.feed) { push in
                    FeedScreen(
                        onNavigateToGame: { push(.game(id: $0)) },
                        onNavigateToProfile: { push(.profile(userId: $0)) },
                        onNavigateToCrafting: { push(.crafting) }
                    )
                }
            }
            Tab(DashboardTab.library.label, systemImage: DashboardTab.library.systemImage, value: .library) {
                stack(.library) { push in
                    LibraryScreen(
                        onNavigateToCrafting: { push(.crafting) },
                        onNavigateToPremium: { push(.premium) },
                        onNavigateToCategory: { push(.libraryDetail(id: $0)) },
                        onNavigateToPlaylist: { push(.libraryDetail(id: $0)) },
                        onNavigateToGame: { push(.game(id: $0)) },
                        onNavigateToProfile: { push(.profile(userId: $0)) }
                    )
                }
            }
            Tab(DashboardTab.account.label, systemImage: DashboardTab.account.systemImage, value: .account) {
                stack(.account) { push in
                    AccountScreen(
                        onNavigateToPremium: { push(.premium) },
                        onNavigateToSettings: { push(.settings) },
                        onNavigateToCrafting: { push(.crafting) },
                        onNavigateToAuthGateway: onNavigateToAuthGateway
                    )
                }
            }
        }
        // Bottom bar on iPhone, sidebar-adaptable on iPad and big screens (DECISIONS §15).
        .tabViewStyle(.sidebarAdaptable)
        .tint(.purpleAccent)
        // The paywall is modal, not part of any tab's history.
        .sheet(isPresented: $isPremiumPresented) {
            PremiumScreen()
                .presentationDragIndicator(.visible)
        }
    }

    private func path(for tab: DashboardTab) -> Binding<[AppRoute]> {
        Binding(get: { paths[tab] ?? [] }, set: { paths[tab] = $0 })
    }

    private func stack<Root: View>(
        _ tab: DashboardTab,
        @ViewBuilder root: (_ push: @escaping (AppRoute) -> Void) -> Root
    ) -> some View {
        let push: (AppRoute) -> Void = { route in
            if route == .premium {
                isPremiumPresented = true
            } else {
                paths[tab, default: []].append(route)
            }
        }
        return NavigationStack(path: path(for: tab)) {
            root(push)
                .background(Color.backgroundDark.ignoresSafeArea())
                .navigationDestination(for: AppRoute.self) {
                    // Pushed pages cover the whole screen, as on Android: the tab bar only shows on tab roots.
                    destination($0, in: tab, push: push)
                        .toolbar(.hidden, for: .tabBar)
                }
        }
    }

    @ViewBuilder
    private func destination(_ route: AppRoute, in tab: DashboardTab, push: @escaping (AppRoute) -> Void) -> some View {
        let back = { _ = paths[tab]?.popLast() }
        switch route {
        case .settings:
            SettingsScreen(onLogoutSuccess: onNavigateToAuthGateway)
        case .settingsSub(.theme): ThemeScreen()
        case .settingsSub(.appIcon): AppIconScreen()
        case .settingsSub(.editProfile): EditProfileScreen()
        case .settingsSub(.changePassword): ChangePasswordScreen()
        case .settingsSub(.notifications): NotificationSettingsScreen()
        case .game(let id):
            GameScreen(gameId: id, onNavigateBack: back)
        case .tutorial:
            TutorialScreen(onNavigateToDashboard: back)
        case .premium:
            // Presented as a sheet by `push`; kept for an exhaustive switch.
            EmptyView()
        case .libraryDetail(let id):
            LibraryDetailScreen(
                categoryId: id,
                onNavigateToGame: { push(.game(id: $0)) },
                onNavigateToProfile: { push(.profile(userId: $0)) }
            )
        case .profile(let userId):
            ProfileScreen(userId: userId, onNavigateToGame: { push(.game(id: $0)) })
        case .crafting:
            CraftingScreen()
        }
    }
}

/// The puzzle editor has not been built on either platform yet (Android shows a stub too).
struct CraftingScreen: View {
    var body: some View {
        ScreenBackground {
            VStack(spacing: 8) {
                Image(systemName: "paintbrush.fill")
                    .font(.system(size: 32))
                    .foregroundColor(.purpleAccent)
                    .padding(.bottom, 8)
                Text(L10n.string("crafting_coming_soon"))
                    .font(.app(13))
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)
            }
            .padding(32)
        }
        .navigationTitle(L10n.string("crafting_title"))
        .navigationBarTitleDisplayMode(.inline)
    }
}
