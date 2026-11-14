import SwiftUI

struct SectionLabel: View {
    let text: String

    var body: some View {
        Text(text.uppercased(with: Locale(identifier: "tr_TR")))
            .font(.app(11, .medium))
            .kerning(1.2)
            .foregroundColor(.textMuted)
            .padding(.leading, 4)
            .padding(.bottom, 8)
    }
}

struct SettingsGroup<Content: View>: View {
    let title: String
    @ViewBuilder var content: Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            SectionLabel(text: title)
                .padding(.bottom, 8)
            DashboardCard(cornerRadius: 20) {
                VStack(spacing: 0) { content }
            }
        }
    }
}

struct SettingsDivider: View {
    var body: some View {
        Rectangle().fill(Color.borderDark).frame(height: 0.5).padding(.horizontal, 16)
    }
}

struct SettingsRow: View {
    enum Trailing { case chevron, externalLink, premium, none }

    let icon: String
    let label: String
    var value: String?
    var trailing: Trailing = .chevron
    var isDestructive = false
    let action: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            Button(action: action) {
                HStack(spacing: 14) {
                    Image(systemName: icon)
                        .font(.system(size: 18))
                        .foregroundColor(isDestructive ? .dangerRed : .textMuted)
                        .frame(width: 22, height: 22)
                    Text(label)
                        .font(.app(15, .medium))
                        .foregroundColor(isDestructive ? .dangerRed : .textPrimary)
                    Spacer(minLength: 8)
                    if let value {
                        Text(value)
                            .font(.app(13))
                            .foregroundColor(.textMuted)
                    }
                    switch trailing {
                    case .premium:
                        Image(systemName: "rosette")
                            .font(.system(size: 16))
                            .foregroundColor(.gameGold)
                            .accessibilityLabel("Premium")
                    case .externalLink:
                        Image(systemName: "arrow.up.right.square")
                            .font(.system(size: 16))
                            .foregroundColor(.textMuted.opacity(0.5))
                    case .chevron:
                        Image(systemName: "chevron.right")
                            .font(.system(size: 12, weight: .semibold))
                            .foregroundColor(.textMuted.opacity(0.5))
                    case .none:
                        EmptyView()
                    }
                }
                .padding(16)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            SettingsDivider()
        }
    }
}

struct SettingsSwitchRow: View {
    var icon: String?
    let label: String
    @Binding var isOn: Bool

    var body: some View {
        VStack(spacing: 0) {
            Toggle(isOn: $isOn) {
                HStack(spacing: 14) {
                    if let icon {
                        Image(systemName: icon)
                            .font(.system(size: 18))
                            .foregroundColor(.textMuted)
                            .frame(width: 22, height: 22)
                    }
                    Text(label)
                        .font(.app(15, .medium))
                        .foregroundColor(.textPrimary)
                }
            }
            .toggleStyle(NeonToggleStyle())
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            SettingsDivider()
        }
    }
}

/// Outlined field used on the edit-profile and change-password screens.
struct OutlinedField: View {
    let label: String
    @Binding var text: String
    var isSecure = false
    var isEnabled = true
    var isMultiline = false
    @FocusState private var isFocused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.app(12))
                .foregroundColor(.textMuted)
            Group {
                if isSecure {
                    SecureField("", text: $text)
                } else if isMultiline {
                    TextField("", text: $text, axis: .vertical)
                        .lineLimit(4...6)
                } else {
                    TextField("", text: $text)
                }
            }
            .font(.app(16))
            .foregroundColor(isEnabled ? .textPrimary : .textMuted)
            .tint(.neonCyan)
            .textInputAutocapitalization(.never)
            .autocorrectionDisabled()
            .focused($isFocused)
            .disabled(!isEnabled)
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
            .background(Color.surfaceDark)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(isFocused ? Color.neonCyan : Color.borderDark, lineWidth: isFocused ? 2 : 1))
        }
    }
}

/// Full-width neon action button at the bottom of a settings form.
struct SettingsActionButton: View {
    let title: String
    var isLoading = false
    var isEnabled = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Group {
                if isLoading {
                    ProgressView().tint(.backgroundDark)
                } else {
                    Text(title)
                        .font(.app(16, .bold))
                        .foregroundColor(.backgroundDark)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 16)
            .background(Color.neonCyan.opacity(isEnabled ? 1 : 0.35))
            .clipShape(RoundedRectangle(cornerRadius: 16))
        }
        .buttonStyle(.plain)
        .disabled(!isEnabled || isLoading)
    }
}

/// Settings subscreen scaffold: grid background, top bar, scrolling content.
struct SettingsScaffold<Content: View>: View {
    let title: String
    let onBack: () -> Void
    @ViewBuilder var content: Content

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {
                ScreenTopBar(title: title, onBack: onBack)
                ScrollView {
                    content
                        .padding(.horizontal, 18)
                        .padding(.vertical, 16)
                }
                .scrollDismissesKeyboard(.interactively)
            }
        }
        .toolbar(.hidden, for: .navigationBar)
    }
}
