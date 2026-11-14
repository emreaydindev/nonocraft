import SwiftUI

struct AppIconScreen: View {
    let onNavigateBack: () -> Void
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
    private let columns = [GridItem(.flexible(), spacing: 16), GridItem(.flexible(), spacing: 16)]

    var body: some View {
        SettingsScaffold(title: "Uygulama İkonu", onBack: onNavigateBack) {
            LazyVGrid(columns: columns, spacing: 16) {
                ForEach(options) { option in
                    let isLocked = option.isPremium && !hasPremium
                    let isSelected = option.id == selectedIcon
                    Button {
                        if !isLocked { selectedIcon = option.id }
                    } label: {
                        VStack(spacing: 0) {
                            RoundedRectangle(cornerRadius: 16)
                                .fill(option.id == "gold" ? Color.gameGold : Color.purpleAccent)
                                .frame(width: 72, height: 72)
                                .overlay {
                                    if isLocked {
                                        ZStack {
                                            Color.backgroundDark.opacity(0.6)
                                            Image(systemName: "lock.fill")
                                                .font(.system(size: 24))
                                                .foregroundColor(.textMuted)
                                        }
                                        .clipShape(RoundedRectangle(cornerRadius: 16))
                                    }
                                }
                                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.borderDark, lineWidth: 1))

                            Text(option.name)
                                .font(.app(14, .semibold))
                                .foregroundColor(isSelected ? .neonCyan : .textPrimary)
                                .padding(.top, 12)

                            Group {
                                if isSelected {
                                    Image(systemName: "checkmark")
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(.backgroundDark)
                                        .frame(width: 24, height: 24)
                                        .background(Color.neonCyan)
                                        .clipShape(Circle())
                                } else if option.isPremium {
                                    Text("PREMIUM")
                                        .font(.app(10, .bold))
                                        .foregroundColor(.gameGold)
                                } else {
                                    Color.clear
                                }
                            }
                            .frame(height: 24)
                            .padding(.top, 8)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(16)
                        .background(isSelected ? Color.neonCyan.opacity(0.1) : Color.surfaceDark)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .overlay(
                            RoundedRectangle(cornerRadius: 20)
                                .stroke(isSelected ? Color.neonCyan : Color.borderDark, lineWidth: isSelected ? 2 : 1)
                        )
                    }
                    .buttonStyle(.plain)
                    .accessibilityAddTraits(isSelected ? .isSelected : [])
                }
            }
        }
    }
}
