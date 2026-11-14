import SwiftUI

/// Back arrow and title used by pushed screens (settings and its subscreens).
struct ScreenTopBar: View {
    let title: String
    let onBack: () -> Void

    var body: some View {
        HStack(spacing: 0) {
            Button(action: onBack) {
                Image(systemName: "arrow.left")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(.textPrimary)
                    .frame(width: 48, height: 48)
            }
            .accessibilityLabel("Geri")
            Text(title)
                .font(.app(22, .bold))
                .foregroundColor(.textPrimary)
            Spacer()
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 12)
    }
}

/// Neon switch matching the Material switch colours used on Android.
struct NeonToggleStyle: ToggleStyle {
    func makeBody(configuration: Configuration) -> some View {
        HStack {
            configuration.label
            Spacer()
            Capsule()
                .fill(configuration.isOn ? Color.neonCyan : Color.surfaceVariantDark)
                .overlay(Capsule().stroke(configuration.isOn ? Color.clear : Color.borderDark, lineWidth: 2))
                .frame(width: 52, height: 32)
                .overlay(alignment: configuration.isOn ? .trailing : .leading) {
                    Circle()
                        .fill(configuration.isOn ? Color.backgroundDark : Color.textMuted)
                        .frame(width: configuration.isOn ? 24 : 16, height: configuration.isOn ? 24 : 16)
                        .padding(.horizontal, configuration.isOn ? 4 : 8)
                }
                .animation(.easeInOut(duration: 0.15), value: configuration.isOn)
                .onTapGesture { configuration.isOn.toggle() }
                .accessibilityAddTraits(.isButton)
        }
    }
}
