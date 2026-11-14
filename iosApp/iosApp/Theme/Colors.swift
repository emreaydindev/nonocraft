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
    static let textSecondary = Color(hex: 0x888888)
    static let textMuted = Color(hex: 0x555555)

    /// Text and spinner colour on top of a neon button.
    static let onNeon = Color(hex: 0x0A0A0F)
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
