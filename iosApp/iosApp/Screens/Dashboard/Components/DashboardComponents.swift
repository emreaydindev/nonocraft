import SwiftUI

enum DashboardTab: CaseIterable, Hashable {
    case playground, feed, library, account

    var label: String {
        switch self {
        case .playground: L10n.string("tab_playground")
        case .feed: L10n.string("tab_explore")
        case .library: L10n.string("tab_library")
        case .account: L10n.string("tab_profile")
        }
    }

    /// SF Symbol for the system tab bar, which fills it when selected.
    var systemImage: String {
        switch self {
        case .playground: "gamecontroller"
        case .feed: "safari"
        case .library: "star.square.on.square"
        case .account: "person.crop.circle"
        }
    }
}

struct DashboardCard<Content: View>: View {
    var backgroundColor: Color = .surfaceDark
    var borderColor: Color = .borderDark
    var cornerRadius: CGFloat = 16
    @ViewBuilder var content: Content

    var body: some View {
        content
            .background(backgroundColor)
            .clipShape(RoundedRectangle(cornerRadius: cornerRadius))
            .overlay(RoundedRectangle(cornerRadius: cornerRadius).stroke(borderColor, lineWidth: 0.5))
    }
}
