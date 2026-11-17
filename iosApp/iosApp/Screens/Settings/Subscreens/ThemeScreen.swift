import SwiftUI

struct ThemeScreen: View {
    // Selection is not applied or persisted yet, same as on Android.
    @State private var selectedTheme = "dark"

    private let freeThemes = [
        ThemeOption(id: "system", name: "Sistem Teması", systemImage: "circle.lefthalf.filled"),
        ThemeOption(id: "dark", name: "Karanlık Mod", systemImage: "moon.fill"),
        ThemeOption(id: "light", name: "Aydınlık Mod", systemImage: "sun.max.fill"),
    ]
    private let premiumThemes = [
        ThemeOption(id: "amoled", name: "AMOLED Siyah", systemImage: "circle.fill"),
        ThemeOption(id: "retro", name: "Retro Wave", systemImage: "sun.horizon.fill"),
        ThemeOption(id: "cyberpunk", name: "Cyberpunk", systemImage: "bolt.fill"),
    ]

    var body: some View {
        SettingsForm(title: "Uygulama Teması") {
            SettingsSection("Standart (Ücretsiz)") {
                ForEach(freeThemes) { row($0, isPremium: false) }
            }
            SettingsSection("Özel Koleksiyon (Premium)") {
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
