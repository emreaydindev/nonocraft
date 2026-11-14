import SwiftUI

struct ThemeScreen: View {
    let onNavigateBack: () -> Void

    // Selection is not applied or persisted yet, same as on Android.
    @State private var selectedTheme = "dark"

    private let freeThemes = [("Sistem Teması", "system"), ("Karanlık Mod", "dark"), ("Aydınlık Mod", "light")]
    private let premiumThemes = [("AMOLED Siyah", "amoled"), ("Retro Wave", "retro"), ("Cyberpunk", "cyberpunk")]

    var body: some View {
        SettingsScaffold(title: "Uygulama Teması", onBack: onNavigateBack) {
            VStack(spacing: 24) {
                themeGroup(title: "Standart (Ücretsiz)", themes: freeThemes, isPremium: false)
                themeGroup(title: "Özel Koleksiyon (Premium)", themes: premiumThemes, isPremium: true)
            }
        }
    }

    private func themeGroup(title: String, themes: [(String, String)], isPremium: Bool) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            SectionLabel(text: title)
            DashboardCard(cornerRadius: 20) {
                VStack(spacing: 0) {
                    ForEach(Array(themes.enumerated()), id: \.element.1) { index, theme in
                        if index > 0 {
                            Rectangle().fill(Color.borderDark).frame(height: 0.5)
                        }
                        ThemeOptionRow(title: theme.0, isSelected: theme.1 == selectedTheme, isPremium: isPremium) {
                            selectedTheme = theme.1
                        }
                    }
                }
            }
        }
    }
}

private struct ThemeOptionRow: View {
    let title: String
    let isSelected: Bool
    let isPremium: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                if isPremium {
                    Image(systemName: "rosette")
                        .font(.system(size: 17))
                        .foregroundColor(.gameGold)
                }
                Text(title)
                    .font(.app(16, isSelected ? .bold : .medium))
                    .foregroundColor(isSelected ? .neonCyan : .textPrimary)
                Spacer()
                if isSelected {
                    Image(systemName: "checkmark")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.neonCyan)
                        .frame(width: 22, height: 22)
                } else {
                    Circle().stroke(Color.borderDark, lineWidth: 2).frame(width: 22, height: 22)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 18)
            .background(isSelected ? Color.neonCyan.opacity(0.05) : .clear)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}
