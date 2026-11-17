import SwiftUI

enum AppFontWeight {
    case regular, medium, semibold, bold

    /// Chakra Petch ships Regular, Medium and Bold; SemiBold falls back to Bold like on Android.
    var fontName: String {
        switch self {
        case .regular: "ChakraPetch-Regular"
        case .medium: "ChakraPetch-Medium"
        case .semibold, .bold: "ChakraPetch-Bold"
        }
    }
}

extension Font {
    /// Chakra Petch at `size` points for the default text size, scaling with Dynamic Type like the system
    /// text style nearest that size (so large headings grow less than body text at accessibility sizes).
    static func app(_ size: CGFloat, _ weight: AppFontWeight = .regular) -> Font {
        .custom(weight.fontName, size: size, relativeTo: textStyle(for: size))
    }

    /// Chakra Petch that never scales: only for numbers drawn inside a fixed cell (board clues, demo grids),
    /// which grow with the board instead.
    static func appFixed(_ size: CGFloat, _ weight: AppFontWeight = .regular) -> Font {
        .custom(weight.fontName, fixedSize: size)
    }

    private static func textStyle(for size: CGFloat) -> Font.TextStyle {
        switch size {
        case 30...: .largeTitle
        case 25..<30: .title
        case 21..<25: .title2
        case 19..<21: .title3
        case 16..<19: .body
        case 14..<16: .subheadline
        case 12..<14: .footnote
        case 11..<12: .caption
        default: .caption2
        }
    }
}
