import UIKit

/// What VoiceOver says about the board: cell positions and states, clues as one phrase per line and palette
/// colours by name. Turkish like the rest of the UI until localization; Android builds the same phrases in
/// `BoardSpeech.kt`.
enum BoardSpeech {
    static func cellPosition(row: Int, col: Int) -> String { "Satır \(row + 1), sütun \(col + 1)" }

    /// 0 empty, -1 crossed, > 0 a palette colour (named only when the puzzle has more than one).
    static func cellState(_ value: Int, paletteHex: [String]) -> String {
        if value == 0 { return "boş" }
        if value < 0 { return "çarpı" }
        return paletteHex.count > 1 ? "dolu, \(colorLabel(value, paletteHex: paletteHex))" : "dolu"
    }

    /// "Satır 3: 1, 1, 1"; in colour puzzles "Satır 3: 2 kırmızı, 1 mavi"; "Satır 3: boş" for an empty line.
    static func clue(isRow: Bool, index: Int, clues: [GridClue], paletteHex: [String]) -> String {
        let parts = clues.filter { $0.count > 0 }.map { clue in
            paletteHex.count > 1 ? "\(clue.count) \(colorName(paletteHex[safe: clue.colorIndex - 1]))" : "\(clue.count)"
        }
        return "\(isRow ? "Satır" : "Sütun") \(index + 1): \(parts.isEmpty ? "boş" : parts.joined(separator: ", "))"
    }

    /// "renk 2, mavi" for palette index 2 (1-based, as in the grid).
    static func colorLabel(_ colorIndex: Int, paletteHex: [String]) -> String {
        "renk \(colorIndex), \(colorName(paletteHex[safe: colorIndex - 1]))"
    }

    /// A plain colour word for a hex colour, from its hue, saturation and brightness.
    static func colorName(_ hex: String?) -> String {
        guard let hex else { return "renkli" }
        let digits = hex.trimmingCharacters(in: CharacterSet(charactersIn: "# "))
        guard digits.count == 6, let rgb = UInt32(digits, radix: 16) else { return "renkli" }
        let color = UIColor(
            red: CGFloat((rgb >> 16) & 0xFF) / 255, green: CGFloat((rgb >> 8) & 0xFF) / 255,
            blue: CGFloat(rgb & 0xFF) / 255, alpha: 1
        )
        var hue: CGFloat = 0, saturation: CGFloat = 0, value: CGFloat = 0, alpha: CGFloat = 0
        color.getHue(&hue, saturation: &saturation, brightness: &value, alpha: &alpha)
        let degrees = hue * 360
        switch true {
        case value < 0.2: return "siyah"
        case saturation < 0.15: return value > 0.85 ? "beyaz" : "gri"
        case degrees < 15 || degrees >= 345: return "kırmızı"
        case degrees < 40: return value < 0.6 ? "kahverengi" : "turuncu"
        case degrees < 65: return "sarı"
        case degrees < 160: return "yeşil"
        case degrees < 195: return "turkuaz"
        case degrees < 250: return "mavi"
        case degrees < 290: return "mor"
        default: return "pembe"
        }
    }
}

/// Fill, cross and clear one cell without drawing (VoiceOver custom actions and the keyboard).
struct BoardCellActions {
    let fill: (_ row: Int, _ col: Int, _ colorIndex: Int) -> Void
    let cross: (_ row: Int, _ col: Int) -> Void
    let clear: (_ row: Int, _ col: Int) -> Void
}
