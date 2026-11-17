import SwiftUI

// The one place that knows about Liquid Glass (DECISIONS §15).
//
// Screens use `.appGlass(...)`, `GlassSurface`, `AppGlassGroup` and `.buttonStyle(.appGlass)` /
// `.buttonStyle(.appGlassProminent)`. They never call `glassEffect` or check `#available(iOS 26, *)`.
//
// - iOS 26+: system `glassEffect` / `.glass` button styles / `GlassEffectContainer`.
// - iOS 18–25: `.ultraThinMaterial` with the same shape, padding and stroke.
// - Reduce Transparency or Increase Contrast (any version): solid themed background with a stronger stroke.

// MARK: - Options

/// How a glass surface looks. Mirrors the system `Glass` variants without exposing them to screens.
struct AppGlassStyle: Equatable {
    enum Variant: Equatable {
        /// Default frosted glass for cards, chips and control bars.
        case regular
        /// More transparent glass for controls floating over rich content (e.g. the game board).
        case clear
    }

    var variant: Variant = .regular
    /// Optional colour wash, e.g. `.neonCyan` for a highlighted chip.
    var tint: Color?
    /// Reacts to touches (scale and shimmer on iOS 26). Use for tappable surfaces that are not `Button`s.
    var interactive = false

    static let regular = AppGlassStyle()
    static let clear = AppGlassStyle(variant: .clear)

    func tint(_ color: Color?) -> AppGlassStyle {
        var copy = self
        copy.tint = color
        return copy
    }

    func interactive(_ isEnabled: Bool = true) -> AppGlassStyle {
        var copy = self
        copy.interactive = isEnabled
        return copy
    }
}

private enum GlassMetrics {
    static let strokeWidth: CGFloat = 1
    static let fallbackStroke = Color.white.opacity(0.12)
    static let solidFill = Color.surfaceDark
    static let solidStroke = Color.borderDark
    static let highContrastStroke = Color.textPrimary.opacity(0.6)
    static let fallbackTintOpacity = 0.22
    static let solidTintOpacity = 0.18
}

// MARK: - Modifier

extension View {
    /// Puts this view on a glass background clipped to `shape`.
    func appGlass<S: Shape>(_ style: AppGlassStyle = .regular, in shape: S) -> some View {
        modifier(AppGlassModifier(style: style, shape: shape))
    }

    /// Puts this view on a capsule-shaped glass background.
    func appGlass(_ style: AppGlassStyle = .regular) -> some View {
        appGlass(style, in: Capsule())
    }
}

private struct AppGlassModifier<S: Shape>: ViewModifier {
    let style: AppGlassStyle
    let shape: S

    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @Environment(\.colorSchemeContrast) private var contrast

    func body(content: Content) -> some View {
        if reduceTransparency || contrast == .increased {
            content
                .background {
                    shape.fill(GlassMetrics.solidFill)
                    if let tint = style.tint {
                        shape.fill(tint.opacity(GlassMetrics.solidTintOpacity))
                    }
                }
                .overlay {
                    shape.stroke(
                        contrast == .increased ? GlassMetrics.highContrastStroke : GlassMetrics.solidStroke,
                        lineWidth: GlassMetrics.strokeWidth
                    )
                }
                .contentShape(shape)
        } else if #available(iOS 26, *) {
            content.glassEffect(systemGlass, in: shape)
        } else {
            content
                .background(
                    style.variant == .clear ? AnyShapeStyle(.ultraThinMaterial.opacity(0.7)) : AnyShapeStyle(.ultraThinMaterial),
                    in: shape
                )
                .background {
                    if let tint = style.tint {
                        shape.fill(tint.opacity(GlassMetrics.fallbackTintOpacity))
                    }
                }
                .overlay { shape.stroke(GlassMetrics.fallbackStroke, lineWidth: GlassMetrics.strokeWidth) }
                .contentShape(shape)
        }
    }

    @available(iOS 26, *)
    private var systemGlass: Glass {
        var glass: Glass = style.variant == .clear ? .clear : .regular
        if let tint = style.tint { glass = glass.tint(tint) }
        return glass.interactive(style.interactive)
    }
}

// MARK: - Container view

/// Padded content on a glass background: cards, floating control bars, chips.
struct GlassSurface<S: Shape, Content: View>: View {
    private let style: AppGlassStyle
    private let shape: S
    private let padding: EdgeInsets
    private let content: Content

    init(
        _ style: AppGlassStyle = .regular,
        in shape: S,
        padding: EdgeInsets = EdgeInsets(top: 12, leading: 16, bottom: 12, trailing: 16),
        @ViewBuilder content: () -> Content
    ) {
        self.style = style
        self.shape = shape
        self.padding = padding
        self.content = content()
    }

    var body: some View {
        content
            .padding(padding)
            .appGlass(style, in: shape)
    }
}

extension GlassSurface where S == RoundedRectangle {
    /// Card-shaped surface (continuous rounded rectangle).
    init(
        _ style: AppGlassStyle = .regular,
        cornerRadius: CGFloat = 20,
        padding: EdgeInsets = EdgeInsets(top: 12, leading: 16, bottom: 12, trailing: 16),
        @ViewBuilder content: () -> Content
    ) {
        self.init(style, in: RoundedRectangle(cornerRadius: cornerRadius, style: .continuous), padding: padding, content: content)
    }
}

// MARK: - Grouping

/// Groups nearby glass elements so they blend and morph together on iOS 26 (`GlassEffectContainer`).
/// On earlier versions it only lays out its content.
struct AppGlassGroup<Content: View>: View {
    private let spacing: CGFloat?
    private let content: Content

    init(spacing: CGFloat? = nil, @ViewBuilder content: () -> Content) {
        self.spacing = spacing
        self.content = content()
    }

    var body: some View {
        if #available(iOS 26, *) {
            GlassEffectContainer(spacing: spacing) { content }
        } else {
            content
        }
    }
}

// MARK: - Buttons

/// Glass button. `.buttonStyle(.appGlass)` for secondary actions, `.buttonStyle(.appGlassProminent)` for the
/// primary one (tinted with the current `.tint`, neon cyan by default).
struct AppGlassButtonStyle: PrimitiveButtonStyle {
    var prominent = false

    func makeBody(configuration: Configuration) -> some View {
        AppGlassButton(configuration: configuration, prominent: prominent)
    }
}

extension PrimitiveButtonStyle where Self == AppGlassButtonStyle {
    static var appGlass: AppGlassButtonStyle { AppGlassButtonStyle() }
    static var appGlassProminent: AppGlassButtonStyle { AppGlassButtonStyle(prominent: true) }
}

private struct AppGlassButton: View {
    let configuration: PrimitiveButtonStyleConfiguration
    let prominent: Bool

    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @Environment(\.colorSchemeContrast) private var contrast

    var body: some View {
        if reduceTransparency || contrast == .increased {
            Button(configuration).buttonStyle(FallbackGlassButtonStyle(prominent: prominent, solid: true))
        } else if #available(iOS 26, *) {
            if prominent {
                Button(configuration).buttonStyle(.glassProminent)
            } else {
                Button(configuration).buttonStyle(.glass)
            }
        } else {
            Button(configuration).buttonStyle(FallbackGlassButtonStyle(prominent: prominent, solid: false))
        }
    }
}

/// iOS 18–25 and accessibility look: capsule with the same padding as the system glass button.
private struct FallbackGlassButtonStyle: ButtonStyle {
    let prominent: Bool
    let solid: Bool

    @Environment(\.isEnabled) private var isEnabled
    @Environment(\.colorSchemeContrast) private var contrast

    func makeBody(configuration: Configuration) -> some View {
        let shape = Capsule()
        configuration.label
            .font(.body.weight(prominent ? .semibold : .medium))
            .foregroundStyle(prominent ? AnyShapeStyle(Color.onNeon) : AnyShapeStyle(Color.textPrimary))
            .padding(.horizontal, 16)
            .padding(.vertical, 10)
            .frame(minHeight: 44)
            .background {
                if prominent {
                    shape.fill(.tint)
                } else if solid {
                    shape.fill(GlassMetrics.solidFill)
                } else {
                    shape.fill(.ultraThinMaterial)
                }
            }
            .overlay {
                if !prominent {
                    shape.stroke(
                        contrast == .increased ? GlassMetrics.highContrastStroke
                            : solid ? GlassMetrics.solidStroke : GlassMetrics.fallbackStroke,
                        lineWidth: GlassMetrics.strokeWidth
                    )
                }
            }
            .contentShape(shape)
            .opacity(isEnabled ? 1 : 0.4)
            .scaleEffect(configuration.isPressed ? 0.96 : 1)
            .brightness(configuration.isPressed ? 0.08 : 0)
            .animation(.easeOut(duration: 0.12), value: configuration.isPressed)
    }
}

// MARK: - Preview

#Preview("Glass wrapper") {
    ZStack {
        LinearGradient(colors: [.purpleAccent, .backgroundDark, .neonCyan], startPoint: .topLeading, endPoint: .bottomTrailing)
            .ignoresSafeArea()
        VStack(spacing: 24) {
            GlassSurface {
                VStack(alignment: .leading, spacing: 6) {
                    Text("Daily puzzle").font(.headline)
                    Text("15×15 · 3 hearts").font(.subheadline).foregroundStyle(.secondary)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }

            AppGlassGroup(spacing: 12) {
                HStack(spacing: 12) {
                    Label("5", systemImage: "diamond.fill").padding(.horizontal, 14).padding(.vertical, 8).appGlass()
                    Label("3", systemImage: "lightbulb.fill").padding(.horizontal, 14).padding(.vertical, 8)
                        .appGlass(.regular.tint(.gameGold))
                }
            }

            HStack(spacing: 12) {
                Button("Hint", systemImage: "lightbulb") {}.buttonStyle(.appGlass)
                Button("Play") {}.buttonStyle(.appGlassProminent)
            }
        }
        .padding(24)
        .foregroundStyle(Color.textPrimary)
        .tint(.neonCyan)
    }
}
