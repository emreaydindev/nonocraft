import SwiftUI

/// Colour-blind support: every palette colour also has its own shape, drawn on filled cells, under colour
/// clues and on the palette swatches, so colours can be told apart without seeing them. Android draws the
/// same shapes in `PaletteGlyph.kt`. Colours past the tenth show their number instead.
enum PaletteGlyph {
    /// UserDefaults key of the "Renk Körü Desenleri" setting; unset means "follow Differentiate Without Colour".
    static let settingKey = "a11yColorPatterns"

    /// Black or white, whichever reads better on `fill`.
    static func contrast(on fill: Color) -> Color {
        var red: CGFloat = 0, green: CGFloat = 0, blue: CGFloat = 0, alpha: CGFloat = 0
        UIColor(fill).getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        let luminance = 0.2126 * red + 0.7152 * green + 0.0722 * blue
        return luminance > 0.45 ? .black.opacity(0.75) : .white.opacity(0.85)
    }

    /// Draws palette colour `colorIndex`'s shape (1-based) centred in `rect`.
    static func draw(_ colorIndex: Int, in rect: CGRect, color: Color, context: inout GraphicsContext) {
        let side = min(rect.width, rect.height)
        let box = CGRect(x: rect.midX - side / 2, y: rect.midY - side / 2, width: side, height: side)
        func p(_ x: CGFloat, _ y: CGFloat) -> CGPoint { CGPoint(x: box.minX + x * side, y: box.minY + y * side) }
        func r(_ x: CGFloat, _ y: CGFloat, _ w: CGFloat, _ h: CGFloat) -> CGRect {
            CGRect(x: box.minX + x * side, y: box.minY + y * side, width: w * side, height: h * side)
        }
        func polygon(_ points: [CGPoint]) -> Path {
            var path = Path()
            path.addLines(points)
            path.closeSubpath()
            return path
        }
        let stroke = StrokeStyle(lineWidth: side * 0.16)
        switch colorIndex {
        case 1: context.fill(Path(ellipseIn: r(0.08, 0.08, 0.84, 0.84)), with: .color(color))
        case 2: context.fill(polygon([p(0.5, 0.06), p(0.95, 0.9), p(0.05, 0.9)]), with: .color(color))
        case 3: context.fill(Path(r(0.12, 0.12, 0.76, 0.76)), with: .color(color))
        case 4: context.fill(polygon([p(0.5, 0), p(1, 0.5), p(0.5, 1), p(0, 0.5)]), with: .color(color))
        case 5:
            context.fill(Path(r(0.36, 0.04, 0.28, 0.92)), with: .color(color))
            context.fill(Path(r(0.04, 0.36, 0.92, 0.28)), with: .color(color))
        case 6: context.stroke(Path(ellipseIn: r(0.14, 0.14, 0.72, 0.72)), with: .color(color), style: stroke)
        case 7: context.fill(Path(r(0.02, 0.34, 0.96, 0.32)), with: .color(color))
        case 8: context.fill(Path(r(0.34, 0.02, 0.32, 0.96)), with: .color(color))
        case 9: context.fill(polygon([p(0.05, 0.1), p(0.95, 0.1), p(0.5, 0.94)]), with: .color(color))
        case 10: context.stroke(Path(r(0.16, 0.16, 0.68, 0.68)), with: .color(color), style: stroke)
        default:
            let text = Text("\(colorIndex)").font(.appFixed(side * 0.8, .bold)).foregroundColor(color)
            context.draw(text, at: CGPoint(x: box.midX, y: box.midY), anchor: .center)
        }
    }
}

/// Whether to draw the shapes: the player's choice, or Differentiate Without Colour until they pick.
struct ColorPatternsReader: DynamicProperty {
    @AppStorage(PaletteGlyph.settingKey) private var stored: Bool?
    @Environment(\.accessibilityDifferentiateWithoutColor) private var differentiateWithoutColor

    var isEnabled: Bool { stored ?? differentiateWithoutColor }

    var binding: Binding<Bool> {
        Binding(get: { stored ?? differentiateWithoutColor }, set: { stored = $0 })
    }
}
