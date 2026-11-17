import SwiftUI

/// Native inset-grouped form on the neon grid background, used by Settings and its subscreens.
/// The navigation bar, back button and title come from the surrounding `NavigationStack`.
struct SettingsForm<Content: View>: View {
    let title: String
    var titleDisplayMode: NavigationBarItem.TitleDisplayMode = .inline
    @ViewBuilder var content: Content

    var body: some View {
        Form { content }
            .scrollContentBackground(.hidden)
            .scrollDismissesKeyboard(.interactively)
            .background { ScreenBackground { EmptyView() } }
            .tint(.neonCyan)
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(titleDisplayMode)
    }
}

/// Form section with the dark card row background.
struct SettingsSection<Content: View, Footer: View>: View {
    let title: String?
    @ViewBuilder var content: Content
    @ViewBuilder var footer: Footer

    var body: some View {
        Section {
            content
        } header: {
            if let title { Text(title) }
        } footer: {
            footer
        }
        .listRowBackground(Color.surfaceDark)
    }
}

extension SettingsSection where Footer == EmptyView {
    init(_ title: String? = nil, @ViewBuilder content: () -> Content) {
        self.init(title: title, content: content, footer: { EmptyView() })
    }
}

/// Row label with a tinted SF Symbol tile, like the system Settings app.
struct SettingsLabel: View {
    let title: String
    let systemImage: String
    var tint: Color = .neonCyan

    @ScaledMetric(relativeTo: .body) private var tileSize: CGFloat = 30

    var body: some View {
        // The tile grows with the text, capped so it doesn't crowd the title at accessibility sizes.
        let side = min(tileSize, 44)
        Label {
            Text(title)
        } icon: {
            Image(systemName: systemImage)
                .font(.system(size: side * 0.47, weight: .semibold))
                .foregroundStyle(tint)
                .frame(width: side, height: side)
                .background(tint.opacity(0.16), in: RoundedRectangle(cornerRadius: 8, style: .continuous))
        }
    }
}

/// Small gold "PRO" capsule for premium-only options (DECISIONS §3).
struct ProBadge: View {
    var body: some View {
        Text("PRO")
            .font(.caption2.weight(.heavy))
            .foregroundStyle(Color.gameGold)
            .padding(.horizontal, 7)
            .padding(.vertical, 3)
            .background(Color.gameGold.opacity(0.18), in: Capsule())
            .accessibilityLabel("Premium")
    }
}

/// Save button for a form's navigation bar; shows a spinner while the request runs.
struct ToolbarSaveButton: ToolbarContent {
    let title: String
    let isLoading: Bool
    let isEnabled: Bool
    let action: () -> Void

    var body: some ToolbarContent {
        ToolbarItem(placement: .confirmationAction) {
            if isLoading {
                ProgressView()
            } else {
                Button(title, action: action)
                    .fontWeight(.semibold)
                    .disabled(!isEnabled)
            }
        }
    }
}
