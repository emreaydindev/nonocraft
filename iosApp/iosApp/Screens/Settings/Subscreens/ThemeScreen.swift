import SwiftUI

struct ThemeScreen: View {
    // Selection is not applied or persisted yet, same as on Android.
    @State private var selectedTheme = "dark"

    private let freeThemes = [
        ThemeOption(id: "system", name: L10n.string("theme_system"), systemImage: "circle.lefthalf.filled"),
        ThemeOption(id: "dark", name: L10n.string("theme_dark"), systemImage: "moon.fill"),
        ThemeOption(id: "light", name: L10n.string("theme_light"), systemImage: "sun.max.fill"),
    ]
    private let premiumThemes = [
        ThemeOption(id: "amoled", name: L10n.string("theme_amoled"), systemImage: "circle.fill"),
        ThemeOption(id: "retro", name: L10n.string("theme_retro"), systemImage: "sun.horizon.fill"),
        ThemeOption(id: "cyberpunk", name: L10n.string("theme_cyberpunk"), systemImage: "bolt.fill"),
    ]

    var body: some View {
        SettingsForm(title: L10n.string("settings_theme")) {
            SettingsSection(L10n.string("theme_standard")) {
                ForEach(freeThemes) { row($0, isPremium: false) }
            }
            SettingsSection(L10n.string("theme_exclusive")) {
                ForEach(premiumThemes) { row($0, isPremium: true) }
            }
        }
    }

    private func row(_ theme: ThemeOption, isPremium: Bool) -> some View {
        let isSelected = theme.id == selectedTheme
        return Button {
            selectedTheme = theme.id
        } label: {
            HStack {
                SettingsLabel(title: theme.name, systemImage: theme.systemImage, tint: isPremium ? .gameGold : .neonCyan)
                if isPremium { ProBadge() }
                Spacer()
                if isSelected {
                    Image(systemName: "checkmark")
                        .fontWeight(.semibold)
                        .foregroundStyle(Color.neonCyan)
                }
            }
        }
        .foregroundStyle(Color.textPrimary)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}

private struct ThemeOption: Identifiable {
    let id: String
    let name: String
    let systemImage: String
}
