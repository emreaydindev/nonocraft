import Shared
import SwiftUI

struct AuthLogoRow: View {
    var body: some View {
        HStack(spacing: 10) {
            Image("Logo")
                .resizable()
                .frame(width: 32, height: 32)
                .clipShape(Circle())
                .frame(width: 36, height: 36)
                .background(Color.neonCyan.opacity(0.10))
                .clipShape(RoundedRectangle(cornerRadius: 9))
                .overlay(RoundedRectangle(cornerRadius: 9).stroke(Color.neonCyan.opacity(0.25), lineWidth: 1))

            BrandName(size: 18)
        }
    }
}

/// "Nonocraft" with the second half in neon.
struct BrandName: View {
    let size: CGFloat
    var leadingColor: Color = .textPrimary

    var body: some View {
        (Text("Nono").foregroundColor(leadingColor) + Text("craft").foregroundColor(.neonCyan))
            .font(.app(size, .bold))
    }
}

struct GoogleSignInButton: View {
    let label: String
    var isLoading = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Group {
                if isLoading {
                    ProgressView()
                } else {
                    HStack(spacing: 12) {
                        Image("GoogleIcon")
                            .resizable()
                            .frame(width: 20, height: 20)
                        Text(label)
                            .font(.body.weight(.semibold))
                            .foregroundStyle(Color.textPrimary)
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 6)
        }
        .buttonStyle(.appGlass)
        .disabled(isLoading)
    }
}

struct AuthDivider: View {
    var label = "veya"

    var body: some View {
        HStack(spacing: 10) {
            Rectangle().fill(Color.borderDark).frame(height: 0.5)
            Text(label)
                .font(.app(12))
                .foregroundColor(.textSecondary)
                .fixedSize()
            Rectangle().fill(Color.borderDark).frame(height: 0.5)
        }
        .padding(.vertical, 16)
    }
}

struct AuthTextField<Trailing: View>: View {
    @Binding var text: String
    let label: String
    let placeholder: String
    var isError = false
    var errorMessage: String?
    var isSecure = false
    var keyboardType: UIKeyboardType = .default
    var contentType: UITextContentType?
    var submitLabel: SubmitLabel = .next
    @ViewBuilder var trailing: Trailing

    private var borderColor: Color {
        if isError { return .dangerRed.opacity(0.70) }
        return text.isEmpty ? .borderDark : .neonCyan.opacity(0.40)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(label)
                .font(.app(11))
                .kerning(0.3)
                .foregroundColor(.textMuted)
                .padding(.bottom, 5)

            HStack(spacing: 8) {
                Group {
                    if isSecure {
                        SecureField("", text: $text, prompt: prompt)
                    } else {
                        TextField("", text: $text, prompt: prompt)
                    }
                }
                .font(.app(16))
                .foregroundColor(.textPrimary)
                .tint(.neonCyan)
                .keyboardType(keyboardType)
                .textContentType(contentType)
                .textInputAutocapitalization(.never)
                .autocorrectionDisabled()
                .submitLabel(submitLabel)

                trailing
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 13)
            .background(Color.surfaceDark)
            .clipShape(RoundedRectangle(cornerRadius: 13))
            .overlay(RoundedRectangle(cornerRadius: 13).stroke(borderColor, lineWidth: 0.5))

            if isError, let errorMessage {
                Text("⚠ \(errorMessage)")
                    .font(.app(11))
                    .foregroundColor(.dangerRed)
                    .padding(.leading, 4)
                    .padding(.top, 5)
            }
        }
    }

    private var prompt: Text {
        Text(placeholder).font(.app(13)).foregroundColor(.textMuted)
    }
}

extension AuthTextField where Trailing == EmptyView {
    init(
        text: Binding<String>,
        label: String,
        placeholder: String,
        isError: Bool = false,
        errorMessage: String? = nil,
        keyboardType: UIKeyboardType = .default,
        contentType: UITextContentType? = nil,
        submitLabel: SubmitLabel = .next
    ) {
        self.init(
            text: text, label: label, placeholder: placeholder, isError: isError, errorMessage: errorMessage,
            keyboardType: keyboardType, contentType: contentType, submitLabel: submitLabel
        ) { EmptyView() }
    }
}

struct AuthPasswordField: View {
    @Binding var text: String
    let label: String
    var placeholder = "••••••••"
    var isError = false
    var errorMessage: String?
    var contentType: UITextContentType = .password
    var submitLabel: SubmitLabel = .done
    @State private var isVisible = false

    var body: some View {
        AuthTextField(
            text: $text, label: label, placeholder: placeholder, isError: isError, errorMessage: errorMessage,
            isSecure: !isVisible, contentType: contentType, submitLabel: submitLabel
        ) {
            Button {
                isVisible.toggle()
            } label: {
                Image(systemName: isVisible ? "eye.slash.fill" : "eye.fill")
                    .font(.system(size: 16))
                    .foregroundColor(.textMuted)
                    .frame(width: 24, height: 24)
            }
            .accessibilityLabel(isVisible ? "Şifreyi gizle" : "Şifreyi göster")
        }
    }
}

enum PasswordStrength {
    case weak, medium, strong

    init(password: String) {
        guard password.count >= 6 else { self = .weak; return }
        let score = [
            password.contains(where: \.isUppercase),
            password.contains(where: \.isNumber),
            password.contains(where: { !$0.isLetter && !$0.isNumber }),
        ].filter { $0 }.count
        if password.count >= 8 && score >= 2 {
            self = .strong
        } else if score >= 1 {
            self = .medium
        } else {
            self = .weak
        }
    }

    var filledSegments: Int {
        switch self {
        case .weak: 1
        case .medium: 2
        case .strong: 3
        }
    }

    var color: Color {
        switch self {
        case .weak: .dangerRed
        case .medium: .gameGold
        case .strong: .neonCyan
        }
    }

    var label: String {
        switch self {
        case .weak: "Zayıf"
        case .medium: "Orta"
        case .strong: "Güçlü"
        }
    }
}

struct PasswordStrengthBar: View {
    let password: String

    var body: some View {
        let strength = PasswordStrength(password: password)
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 4) {
                ForEach(0..<3, id: \.self) { index in
                    RoundedRectangle(cornerRadius: 2)
                        .fill(index < strength.filledSegments ? strength.color : .surfaceVariantDark)
                        .frame(height: 3)
                }
            }
            .padding(.top, 6)
            .padding(.bottom, 4)

            Text(strength.label)
                .font(.app(11))
                .foregroundColor(strength.color.opacity(0.80))
                .padding(.leading, 2)
        }
    }
}

struct PasswordRequirementRow: View {
    let text: String
    let met: Bool

    var body: some View {
        HStack(spacing: 8) {
            Text(met ? "✓" : "·")
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(met ? .neonCyan : .textMuted)
                .frame(width: 16, height: 16)
                .background(met ? Color.neonCyan.opacity(0.15) : Color.surfaceVariantDark)
                .clipShape(Circle())
            Text(text)
                .font(.app(12))
                .foregroundColor(met ? .neonCyan.opacity(0.85) : .textMuted)
        }
        .padding(.vertical, 3)
    }
}

/// Full-width primary action: prominent glass tinted neon cyan.
struct PrimaryAuthButton: View {
    let text: String
    var enabled = true
    var isLoading = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Group {
                if isLoading {
                    ProgressView().tint(.onNeon)
                } else {
                    Text(text)
                        .font(.app(16, .bold))
                        .kerning(0.5)
                        .foregroundStyle(Color.onNeon)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 6)
        }
        .buttonStyle(.appGlassProminent)
        .tint(.neonCyan)
        .disabled(!enabled || isLoading)
    }
}

/// Full-width secondary action: regular glass with neon text.
struct OutlineAuthButton: View {
    let text: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.body.weight(.semibold))
                .foregroundStyle(Color.neonCyan)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 6)
        }
        .buttonStyle(.appGlass)
    }
}

/// Low-emphasis text action.
struct GhostAuthButton: View {
    let text: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.subheadline.weight(.medium))
                .foregroundStyle(Color.textSecondary)
                .frame(maxWidth: .infinity, minHeight: 44)
                .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

struct InfoBanner: View {
    let message: String
    var subMessage: String?
    var isError = false

    private var tint: Color { isError ? .dangerRed : .neonCyan }

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text(isError ? "⚠" : "✓")
                .font(.system(size: 15))
                .foregroundColor(tint)
            VStack(alignment: .leading, spacing: 2) {
                Text(message)
                    .font(.app(13, .medium))
                    .foregroundColor(tint)
                if let subMessage {
                    Text(subMessage)
                        .font(.app(11))
                        .foregroundColor(tint.opacity(0.65))
                }
            }
            Spacer(minLength: 0)
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .background(tint.opacity(isError ? 0.10 : 0.08))
        .clipShape(RoundedRectangle(cornerRadius: 13))
        .overlay(RoundedRectangle(cornerRadius: 13).stroke(tint.opacity(isError ? 0.35 : 0.30), lineWidth: 0.5))
    }
}

/// "By continuing you accept the Privacy Policy and Terms" with tappable links.
struct LegalText: View {
    var body: some View {
        Text(attributed)
            .font(.app(11))
            .lineSpacing(3)
            .multilineTextAlignment(.center)
            .tint(.neonCyan)
            .frame(maxWidth: .infinity)
    }

    private var attributed: AttributedString {
        var result = AttributedString("Devam ederek ")
        result.foregroundColor = .textSecondary
        result += link("Gizlilik Politikası", Constants.shared.PRIVACY_POLICY_URL)
        var and = AttributedString(" ve ")
        and.foregroundColor = .textSecondary
        result += and
        result += link("Kullanım Koşulları", Constants.shared.TERMS_OF_SERVICE_URL)
        var tail = AttributedString("'nı kabul etmiş olursunuz.")
        tail.foregroundColor = .textSecondary
        result += tail
        return result
    }

    private func link(_ title: String, _ url: String) -> AttributedString {
        var part = AttributedString(title)
        part.link = URL(string: url)
        part.foregroundColor = .neonCyan
        part.font = .app(11, .semibold)
        part.underlineStyle = .single
        return part
    }
}
