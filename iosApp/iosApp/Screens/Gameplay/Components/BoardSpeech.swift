import UIKit

/// What VoiceOver says about the board: cell positions and states, clues as one phrase per line and palette
/// colours by name, in the app language. Android builds the same phrases from the same keys in `BoardSpeech.kt`.
enum BoardSpeech {
    static func cellPosition(row: Int, col: Int) -> String { L10n.format("board_cell_position", row + 1, col + 1) }

    /// 0 empty, -1 crossed, > 0 a palette colour (named only when the puzzle has more than one).
    static func cellState(_ value: Int, paletteHex: [String]) -> String {
        if value == 0 { return L10n.string("board_empty") }
        if value < 0 { return L10n.string("board_crossed") }
        return paletteHex.count > 1
            ? L10n.format("board_filled_color", colorLabel(value, paletteHex: paletteHex))
            : L10n.string("board_filled")
    }

    /// "Row 3: 1, 1, 1"; in colour puzzles "Row 3: 2 red, 1 blue"; "Row 3: empty" for an empty line.
    static func clue(isRow: Bool, index: Int, clues: [GridClue], paletteHex: [String]) -> String {
        let parts = clues.filter { $0.count > 0 }.map { clue in
            paletteHex.count > 1
                ? L10n.format("board_clue_part", clue.count, colorName(paletteHex[safe: clue.colorIndex - 1]))
                : "\(clue.count)"
        }
        let text = parts.isEmpty ? L10n.string("board_empty") : parts.joined(separator: ", ")
        return L10n.format(isRow ? "board_clue_row" : "board_clue_column", index + 1, text)
    }

    /// "colour 2, blue" for palette index 2 (1-based, as in the grid).
    static func colorLabel(_ colorIndex: Int, paletteHex: [String]) -> String {
        L10n.format("board_color_label", colorIndex, colorName(paletteHex[safe: colorIndex - 1]))
    }

    /// A plain colour word for a hex colour, from its hue, saturation and brightness.
    static func colorName(_ hex: String?) -> String { L10n.string(colorNameKey(hex)) }

    /// The catalog key of the colour word for a hex colour (`color_red`, ...).
    static func colorNameKey(_ hex: String?) -> String {
        guard let hex else { return "color_unknown" }
        let digits = hex.trimmingCharacters(in: CharacterSet(charactersIn: "# "))
        guard digits.count == 6, let rgb = UInt32(digits, radix: 16) else { return "color_unknown" }
        let color = UIColor(
            red: CGFloat((rgb >> 16) & 0xFF) / 255, green: CGFloat((rgb >> 8) & 0xFF) / 255,
            blue: CGFloat(rgb & 0xFF) / 255, alpha: 1
        )
        var hue: CGFloat = 0, saturation: CGFloat = 0, value: CGFloat = 0, alpha: CGFloat = 0
        color.getHue(&hue, saturation: &saturation, brightness: &value, alpha: &alpha)
        let degrees = hue * 360
        switch true {
        case value < 0.2: return "color_black"
        case saturation < 0.15: return value > 0.85 ? "color_white" : "color_grey"
        case degrees < 15 || degrees >= 345: return "color_red"
        case degrees < 40: return value < 0.6 ? "color_brown" : "color_orange"
        case degrees < 65: return "color_yellow"
        case degrees < 160: return "color_green"
        case degrees < 195: return "color_turquoise"
        case degrees < 250: return "color_blue"
        case degrees < 290: return "color_purple"
        default: return "color_pink"
        }
    }
}

/// Fill, cross and clear one cell without drawing (VoiceOver custom actions and the keyboard).
struct BoardCellActions {
    let fill: (_ row: Int, _ col: Int, _ colorIndex: Int) -> Void
    let cross: (_ row: Int, _ col: Int) -> Void
    let clear: (_ row: Int, _ col: Int) -> Void
}
