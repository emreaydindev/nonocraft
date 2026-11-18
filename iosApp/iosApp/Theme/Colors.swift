import SwiftUI

extension Color {
    init(hex: UInt32) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255
        )
    }

    static let backgroundDark = Color(hex: 0x121212)
    static let surfaceDark = Color(hex: 0x1C1C1E)
    static let surfaceVariantDark = Color(hex: 0x242426)
    static let borderDark = Color(hex: 0x2A2A2C)

    static let neonCyan = Color(hex: 0x00FFDC)
    static let purpleAccent = Color(hex: 0xB64FF6)
    static let gameGold = Color(hex: 0xFFD700)
    static let dangerRed = Color(hex: 0xE24B4A)
    static let brightGreen = Color(hex: 0x3BE813)

    static let textPrimary = Color(hex: 0xF5F5F5)
    // Secondary and muted text meet 4.5:1 on every surface (background, surface, surface variant) and get
    // brighter still with Increase Contrast.
    static let textSecondary = Color(hex: 0xA0A0A0, increasedContrast: 0xC8C8C8)
    static let textMuted = Color(hex: 0x8E8E8E, increasedContrast: 0xB4B4B4)

    /// Text and spinner colour on top of a neon button.
    static let onNeon = Color(hex: 0x0A0A0F)
}

extension Color {
    /// A colour that switches to `increasedContrast` when Increase Contrast is on.
    init(hex: UInt32, increasedContrast: UInt32) {
        func uiColor(_ value: UInt32) -> UIColor {
            UIColor(
                red: CGFloat((value >> 16) & 0xFF) / 255, green: CGFloat((value >> 8) & 0xFF) / 255,
                blue: CGFloat(value & 0xFF) / 255, alpha: 1
            )
        }
        self.init(uiColor: UIColor { traits in
            uiColor(traits.accessibilityContrast == .high ? increasedContrast : hex)
        })
    }
}

extension Color {
    /// ARGB value as used by the shared module, e.g. 0xFFE24B4A.
    init(argb: Int64) {
        self.init(hex: UInt32(truncatingIfNeeded: argb) & 0xFFFFFF)
    }
}

extension Color {
    /// Parses "#RRGGBB"; falls back to grey like the Android `String.toColor()`.
    init(hexString: String) {
        let digits = hexString.trimmingCharacters(in: CharacterSet(charactersIn: "# "))
        if digits.count == 6, let value = UInt32(digits, radix: 16) {
            self.init(hex: value)
        } else {
            self = .gray
        }
    }
}

extension Color {
    /// True when the colour's relative luminance is low enough to disappear on the dark surfaces.
    var isDark: Bool {
        var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0, alpha: CGFloat = 0
        guard UIColor(self).getRed(&red, green: &green, blue: &blue, alpha: &alpha) else { return false }
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue < 0.2
    }
}
