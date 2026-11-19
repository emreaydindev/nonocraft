import SwiftUI

/// Thumb stick that moves the board cursor one cell every 150 ms while held past 40 % of its travel.
struct AnalogStick: View {
    let onMove: (_ dRow: Int, _ dCol: Int) -> Void
    let onFocusChange: (Bool) -> Void
    /// VoiceOver: the cell under the cursor ("Row 2, column 3, empty").
    let cursorDescription: String
    /// VoiceOver moves: one cell up, down, left or right, or the next / previous cell in reading order.
    let onAccessibilityMove: (_ dRow: Int, _ dCol: Int) -> Void
    let onAccessibilityStep: (_ forward: Bool) -> Void

    private let stickSize: CGFloat = 100
    private let thumbSize: CGFloat = 44
    private var maxRadius: CGFloat { (stickSize - thumbSize) / 2 }

    @State private var thumbOffset: CGSize = .zero
    @State private var isDragging = false
    @State private var repeatTask: Task<Void, Never>?

    var body: some View {
        ZStack {
            Circle()
                .fill(Color.surfaceDark.opacity(0.5))
                .overlay(Circle().stroke(Color.borderDark, lineWidth: 2))

            ForEach([CGSize(width: 0, height: -30), CGSize(width: 0, height: 30), CGSize(width: -30, height: 0), CGSize(width: 30, height: 0)], id: \.debugDescription) { offset in
                Circle().fill(Color.borderDark).frame(width: 8, height: 8).offset(offset)
            }

            Circle()
                .fill(Color.surfaceVariantDark)
                .overlay(Circle().stroke(Color.textPrimary.opacity(0.3), lineWidth: 1))
                .frame(width: thumbSize, height: thumbSize)
                .offset(thumbOffset)
        }
        .frame(width: stickSize, height: stickSize)
        .contentShape(Circle())
        .gesture(
            DragGesture(minimumDistance: 0)
                .onChanged { value in
                    if !isDragging {
                        isDragging = true
                        onFocusChange(true)
                        startRepeating()
                    }
                    let distance = hypot(value.translation.width, value.translation.height)
                    let scale = distance > maxRadius ? maxRadius / distance : 1
                    thumbOffset = CGSize(width: value.translation.width * scale, height: value.translation.height * scale)
                }
                .onEnded { _ in release() }
        )
        .onDisappear(perform: release)
        // The stick is the board cursor for VoiceOver: swipe up or down steps through the cells, the actions
        // move it a cell at a time, and the place button then fills, crosses or clears the cell under it.
        .accessibilityElement()
        .accessibilityLabel(L10n.string("game_cursor"))
        .accessibilityValue(cursorDescription)
        .accessibilityAdjustableAction { direction in
            switch direction {
            case .increment: onAccessibilityStep(true)
            case .decrement: onAccessibilityStep(false)
            @unknown default: break
            }
        }
        .accessibilityActions {
            // Declared in reverse: VoiceOver lists them Up, Down, Left, Right.
            Button(L10n.string("game_move_right")) { onAccessibilityMove(0, 1) }
            Button(L10n.string("game_move_left")) { onAccessibilityMove(0, -1) }
            Button(L10n.string("game_move_down")) { onAccessibilityMove(1, 0) }
            Button(L10n.string("game_move_up")) { onAccessibilityMove(-1, 0) }
        }
    }

    private func startRepeating() {
        repeatTask?.cancel()
        repeatTask = Task { @MainActor in
            while !Task.isCancelled {
                let offset = thumbOffset
                if hypot(offset.width, offset.height) > maxRadius * 0.4 {
                    let vertical = abs(offset.height) > abs(offset.width)
                    onMove(vertical ? (offset.height > 0 ? 1 : -1) : 0, vertical ? 0 : (offset.width > 0 ? 1 : -1))
                }
                try? await Task.sleep(for: .milliseconds(150))
            }
        }
    }

    private func release() {
        guard isDragging else { return }
        isDragging = false
        repeatTask?.cancel()
        repeatTask = nil
        withAnimation(.easeOut(duration: 0.12)) { thumbOffset = .zero }
        onFocusChange(false)
    }
}

/// Floating glass bar under the board: tool palette on top, stick and actions below.
/// The controls inside are flat (no glass on glass); only the bar itself is glass.
struct GameControlBar: View {
    let palette: [Color]
    let colorLabels: [String]
    var showColorPatterns = false
    let selectedColorIndex: Int
    let isCrossMode: Bool
    let hintsRemaining: Int
    let cursorDescription: String
    let onMove: (_ dRow: Int, _ dCol: Int) -> Void
    let onAccessibilityMove: (_ dRow: Int, _ dCol: Int) -> Void
    let onAccessibilityStep: (_ forward: Bool) -> Void
    let onStickFocusChange: (Bool) -> Void
    let onPlacePressed: (Bool) -> Void
    let onHint: () -> Void
    let onColorSelected: (Int) -> Void
    let onCrossSelected: () -> Void

    var body: some View {
        GlassSurface(cornerRadius: 32, padding: EdgeInsets(top: 12, leading: 16, bottom: 16, trailing: 16)) {
            VStack(spacing: 12) {
                ToolPaletteBar(
                    palette: palette,
                    colorLabels: colorLabels,
                    showColorPatterns: showColorPatterns,
                    selectedColorIndex: selectedColorIndex,
                    isCrossMode: isCrossMode,
                    onColorSelected: onColorSelected,
                    onCrossSelected: onCrossSelected
                )
                HStack {
                    AnalogStick(
                        onMove: onMove,
                        onFocusChange: onStickFocusChange,
                        cursorDescription: cursorDescription,
                        onAccessibilityMove: onAccessibilityMove,
                        onAccessibilityStep: onAccessibilityStep
                    )
                    Spacer(minLength: 16)
                    GameActionButtons(
                        hintsRemaining: hintsRemaining,
                        selectedColor: isCrossMode ? .dangerRed : palette[safe: selectedColorIndex - 1] ?? .neonCyan,
                        isCrossMode: isCrossMode,
                        onPlacePressed: onPlacePressed,
                        onHint: onHint
                    )
                }
            }
        }
        // Fixed-size round controls: the hint count may grow a little, never past its circle. VoiceOver reads
        // the full count from the button label.
        .dynamicTypeSize(...DynamicTypeSize.xLarge)
        .frame(maxWidth: 560)
        .padding(.horizontal, 12)
        .padding(.bottom, 4)
    }
}

struct GameActionButtons: View {
    let hintsRemaining: Int
    let selectedColor: Color
    let isCrossMode: Bool
    let onPlacePressed: (Bool) -> Void
    let onHint: () -> Void

    @State private var isPlaceHeld = false

    var body: some View {
        HStack(spacing: 16) {
            Button(action: onHint) {
                HStack(spacing: 4) {
                    Image(systemName: "lightbulb.fill").font(.system(size: 20))
                    Text("\(hintsRemaining)").font(.app(18, .bold))
                }
                .foregroundColor(.neonCyan)
                .frame(width: 64, height: 64)
                .background(Color.neonCyan.opacity(0.1))
                .clipShape(Circle())
                .overlay(Circle().stroke(Color.neonCyan.opacity(0.5), lineWidth: 2))
            }
            .buttonStyle(.plain)
            .accessibilityLabel(L10n.format("game_hints_left", Int(hintsRemaining)))

            // Held down while the stick moves, it fills every cell the cursor enters.
            Image(systemName: isCrossMode ? "xmark" : "pencil")
                .font(.system(size: 30, weight: .bold))
                .foregroundColor(selectedColor)
                .frame(width: 76, height: 76)
                .background(selectedColor.opacity(isPlaceHeld ? 0.35 : 0.15))
                .clipShape(Circle())
                .overlay(Circle().stroke(selectedColor, lineWidth: 3))
                .contentShape(Circle())
                .gesture(
                    DragGesture(minimumDistance: 0)
                        .onChanged { _ in
                            guard !isPlaceHeld else { return }
                            isPlaceHeld = true
                            onPlacePressed(true)
                        }
                        .onEnded { _ in
                            isPlaceHeld = false
                            onPlacePressed(false)
                        }
                )
                .accessibilityLabel(L10n.string(isCrossMode ? "game_place_cross" : "game_place_fill"))
                .accessibilityAddTraits(.isButton)
                // Held, it keeps placing while the stick moves; for VoiceOver a double tap places once.
                .accessibilityAction {
                    onPlacePressed(true)
                    onPlacePressed(false)
                }
        }
    }
}

struct ToolPaletteBar: View {
    let palette: [Color]
    let colorLabels: [String]
    var showColorPatterns = false
    let selectedColorIndex: Int
    let isCrossMode: Bool
    let onColorSelected: (Int) -> Void
    let onCrossSelected: () -> Void

    var body: some View {
        // Centred when it fits, scrolls sideways for large palettes.
        ViewThatFits(in: .horizontal) {
            items
            ScrollView(.horizontal, showsIndicators: false) { items }
        }
    }

    private var items: some View {
        HStack(spacing: 12) {
            ToolItem(tint: .dangerRed, isSelected: isCrossMode, label: L10n.string("game_cross_tool"), action: onCrossSelected) {
                Image(systemName: "xmark")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundColor(.dangerRed)
            }
            ForEach(Array(palette.enumerated()), id: \.offset) { index, color in
                let colorIndex = index + 1
                ToolItem(
                    tint: color, isSelected: !isCrossMode && selectedColorIndex == colorIndex,
                    label: colorLabels[safe: index] ?? L10n.format("game_color_n", colorIndex), action: { onColorSelected(colorIndex) }
                ) {
                    RoundedRectangle(cornerRadius: 4)
                        .fill(color)
                        .overlay {
                            // Dark swatches (e.g. black) vanish on the dark glass without an outline.
                            if color.isDark {
                                RoundedRectangle(cornerRadius: 4).stroke(Color.textPrimary.opacity(0.5), lineWidth: 1)
                            }
                        }
                        .overlay {
                            if showColorPatterns && palette.count > 1 {
                                Canvas { context, size in
                                    PaletteGlyph.draw(
                                        colorIndex, in: CGRect(origin: .zero, size: size).insetBy(dx: 5, dy: 5),
                                        color: PaletteGlyph.contrast(on: color), context: &context
                                    )
                                }
                                .accessibilityHidden(true)
                            }
                        }
                        .frame(width: 28, height: 28)
                }
            }
        }
        .padding(.horizontal, 2)
    }
}

private struct ToolItem<Content: View>: View {
    let tint: Color
    let isSelected: Bool
    let label: String
    let action: () -> Void
    @ViewBuilder var content: Content

    var body: some View {
        Button(action: action) {
            content
                .frame(width: 48, height: 48)
                .background(isSelected ? tint.opacity(0.2) : .clear)
                .clipShape(Circle())
                .overlay(Circle().stroke(isSelected ? tint : Color.borderDark, lineWidth: 2))
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}
