import SwiftUI

struct AppIconScreen: View {
    var hasPremium = false

    private struct Option: Identifiable {
        let id: String
        let name: String
        let isPremium: Bool
    }

    // Selection is not applied or persisted yet, same as on Android.
    @State private var selectedIcon = "classic"

    private let options = [
        Option(id: "classic", name: "Klasik", isPremium: false),
        Option(id: "dark", name: "Gece Modu", isPremium: false),
        Option(id: "neon", name: "Neon Küp", isPremium: true),
        Option(id: "gold", name: "Altın Taç", isPremium: true),
        Option(id: "pixel", name: "8-Bit Nostalji", isPremium: true),
        Option(id: "synth", name: "Synthwave", isPremium: true),
    ]

    var body: some View {
        SettingsForm(title: "Uygulama İkonu") {
            SettingsSection {
                ForEach(options.filter { !$0.isPremium }) { row($0) }
            }
            SettingsSection("Premium") {
                ForEach(options.filter(\.isPremium)) { row($0) }
            }
        }
    }

    private func row(_ option: Option) -> some View {
        let isLocked = option.isPremium && !hasPremium
        let isSelected = option.id == selectedIcon
        return Button {
            if !isLocked { selectedIcon = option.id }
        } label: {
            HStack(spacing: 14) {
                // Placeholder artwork until the alternate icons are added to the asset catalog.
                RoundedRectangle(cornerRadius: 13, style: .continuous)
                    .fill(option.id == "gold" ? Color.gameGold : Color.purpleAccent)
                    .frame(width: 56, height: 56)
                    .overlay {
                        if isLocked {
                            Image(systemName: "lock.fill")
                                .font(.title3)
                                .foregroundStyle(.white)
                                .frame(maxWidth: .infinity, maxHeight: .infinity)
                                .background(.black.opacity(0.45), in: RoundedRectangle(cornerRadius: 13, style: .continuous))
                        }
                    }
                    .overlay(RoundedRectangle(cornerRadius: 13, style: .continuous).stroke(.white.opacity(0.12)))
                Text(option.name)
                    .foregroundStyle(isLocked ? Color.textSecondary : Color.textPrimary)
                if option.isPremium { ProBadge() }
                Spacer()
                if isSelected {
                    Image(systemName: "checkmark")
                        .fontWeight(.semibold)
                        .foregroundStyle(Color.neonCyan)
                }
            }
            .padding(.vertical, 4)
        }
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}
