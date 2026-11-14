import SwiftUI

enum DashboardTab: CaseIterable, Hashable {
    case playground, feed, library, account

    var label: String {
        switch self {
        case .playground: "Oyun Alanı"
        case .feed: "Keşfet"
        case .library: "Koleksiyon"
        case .account: "Profil"
        }
    }

    func icon(selected: Bool) -> String {
        switch self {
        case .playground: selected ? "gamecontroller.fill" : "gamecontroller"
        case .feed: selected ? "safari.fill" : "safari"
        case .library: selected ? "star.square.on.square.fill" : "star.square.on.square"
        case .account: selected ? "person.crop.circle.fill" : "person.crop.circle"
        }
    }
}

struct DashboardBottomNavBar: View {
    @Binding var currentTab: DashboardTab

    var body: some View {
        HStack(spacing: 0) {
            ForEach(DashboardTab.allCases, id: \.self) { tab in
                let isSelected = tab == currentTab
                Button {
                    currentTab = tab
                } label: {
                    VStack(spacing: 4) {
                        Image(systemName: tab.icon(selected: isSelected))
                            .font(.system(size: 20))
                            .frame(width: 64, height: 32)
                            .background(isSelected ? Color.purpleAccent.opacity(0.15) : .clear)
                            .clipShape(Capsule())
                        Text(tab.label)
                            .font(.app(11, isSelected ? .semibold : .medium))
                    }
                    .foregroundColor(isSelected ? .purpleAccent : .textMuted)
                    .frame(maxWidth: .infinity)
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                .accessibilityAddTraits(isSelected ? .isSelected : [])
            }
        }
        .padding(.top, 12)
        .padding(.bottom, 4)
        .background(Color.surfaceDark.ignoresSafeArea(edges: .bottom))
        .overlay(alignment: .top) {
            Rectangle().fill(Color.borderDark).frame(height: 0.5)
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

/// Neon pill button used inside dashboard cards ("Oyna", "Devam Et").
struct NeonPillButton: View {
    let title: String
    var verticalPadding: CGFloat = 10
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.app(14, .bold))
                .foregroundColor(.onNeon)
                .padding(.horizontal, 18)
                .padding(.vertical, verticalPadding)
                .background(Color.neonCyan)
                .clipShape(RoundedRectangle(cornerRadius: 14))
        }
        .buttonStyle(.plain)
    }
}
