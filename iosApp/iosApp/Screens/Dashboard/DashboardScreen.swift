import SwiftUI

struct DashboardScreen: View {
    @State private var currentTab: DashboardTab = .playground

    var body: some View {
        VStack(spacing: 0) {
            Group {
                switch currentTab {
                case .playground:
                    PlaygroundScreen(
                        onNavigateToGame: { _ in },
                        onNavigateToSettings: {},
                        onNavigateToTutorial: {}
                    )
                case .feed, .library, .account:
                    NotPortedYetView(title: currentTab.label)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            DashboardBottomNavBar(currentTab: $currentTab)
        }
        .background(Color.backgroundDark.ignoresSafeArea())
    }
}

/// Stand-in for tabs whose SwiftUI port has not been written yet.
private struct NotPortedYetView: View {
    let title: String

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
    }
}
