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
    static func app(_ size: CGFloat, _ weight: AppFontWeight = .regular) -> Font {
        .custom(weight.fontName, fixedSize: size)
    }
}
