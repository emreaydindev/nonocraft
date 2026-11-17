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
