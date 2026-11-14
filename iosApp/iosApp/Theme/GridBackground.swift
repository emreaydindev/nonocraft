import SwiftUI

/// Faint neon grid drawn behind every screen.
struct GridBackground: View {
    var color: Color = .neonCyan
    var opacity: Double = 0.06
    /// Android draws the grid every 72 px, which is about 27 pt on a 2.625x screen.
    var step: CGFloat = 27

    var body: some View {
        Canvas { context, size in
            var path = Path()
            var x: CGFloat = 0
            while x <= size.width {
                path.move(to: CGPoint(x: x, y: 0))
                path.addLine(to: CGPoint(x: x, y: size.height))
                x += step
            }
            var y: CGFloat = 0
            while y <= size.height {
                path.move(to: CGPoint(x: 0, y: y))
                path.addLine(to: CGPoint(x: size.width, y: y))
                y += step
            }
            context.stroke(path, with: .color(color.opacity(opacity)), lineWidth: 0.5)
        }
        .ignoresSafeArea()
        .allowsHitTesting(false)
    }
}

/// Screen scaffold: dark background with the grid, content on top.
struct ScreenBackground<Content: View>: View {
    @ViewBuilder var content: Content

    var body: some View {
        ZStack {
            Color.backgroundDark.ignoresSafeArea()
            GridBackground()
            content
        }
    }
}
