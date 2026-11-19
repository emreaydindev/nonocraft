import SwiftUI

/// Native inset-grouped form on the neon grid background, used by Settings and its subscreens.
/// The navigation bar, back button and title come from the surrounding `NavigationStack`.
struct SettingsForm<Content: View>: View {
    let title: String
    var titleDisplayMode: NavigationBarItem.TitleDisplayMode = .inline
    @ViewBuilder var content: Content

    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        Form { content }
            .modifier(StackedValuesModifier(isActive: dynamicTypeSize.isAccessibilitySize))
            .scrollContentBackground(.hidden)
            .scrollDismissesKeyboard(.interactively)
            .background { ScreenBackground { EmptyView() } }
            .tint(.neonCyan)
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(titleDisplayMode)
    }
}

/// At accessibility text sizes the row value (Dark, Classic PRO, English) goes under the title, so neither is
/// squeezed into a narrow column. iOS 26 and later already do this; iOS 18-25 keep them side by side.
private struct StackedValuesModifier: ViewModifier {
    let isActive: Bool

    func body(content: Content) -> some View {
        if isActive {
            content.labeledContentStyle(StackedLabeledContentStyle())
        } else {
            content
        }
    }
}

private struct StackedLabeledContentStyle: LabeledContentStyle {
    func makeBody(configuration: Configuration) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            configuration.label
            configuration.content
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
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
    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    var body: some View {
        if dynamicTypeSize.isAccessibilitySize {
            // The tile sits above the title so long words (Uygulama, Customisation) keep a full line.
            VStack(alignment: .leading, spacing: 6) {
                tile
                Text(title)
            }
        } else {
            Label {
                Text(title)
            } icon: {
                tile
            }
        }
    }

    private var tile: some View {
        // The tile grows with the text, capped at accessibility sizes.
        let side = min(tileSize, 44)
        return Image(systemName: systemImage)
            .font(.system(size: side * 0.47, weight: .semibold))
            .foregroundStyle(tint)
            .frame(width: side, height: side)
            .background(tint.opacity(0.16), in: RoundedRectangle(cornerRadius: 8, style: .continuous))
            .accessibilityHidden(true)
    }
}

/// Small gold "PRO" capsule for premium-only options (DECISIONS §3).
struct ProBadge: View {
    var body: some View {
        Text(L10n.string("common_pro_badge"))
            .font(.caption2.weight(.heavy))
            .foregroundStyle(Color.gameGold)
            .padding(.horizontal, 7)
            .padding(.vertical, 3)
            .background(Color.gameGold.opacity(0.18), in: Capsule())
            .accessibilityLabel(L10n.string("common_premium"))
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
