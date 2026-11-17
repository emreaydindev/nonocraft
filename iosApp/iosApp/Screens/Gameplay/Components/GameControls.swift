import SwiftUI

/// Thumb stick that moves the board cursor one cell every 150 ms while held past 40 % of its travel.
struct AnalogStick: View {
    let onMove: (_ dRow: Int, _ dCol: Int) -> Void
    let onFocusChange: (Bool) -> Void

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
        .accessibilityLabel("İmleç kontrolü")
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
    let selectedColorIndex: Int
    let isCrossMode: Bool
    let hintsRemaining: Int
    let onMove: (_ dRow: Int, _ dCol: Int) -> Void
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
                    selectedColorIndex: selectedColorIndex,
                    isCrossMode: isCrossMode,
                    onColorSelected: onColorSelected,
                    onCrossSelected: onCrossSelected
                )
                HStack {
                    AnalogStick(onMove: onMove, onFocusChange: onStickFocusChange)
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
            .accessibilityLabel("İpucu, \(hintsRemaining) kaldı")

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
                .accessibilityLabel(isCrossMode ? "Çarpı koy" : "Doldur")
                .accessibilityAddTraits(.isButton)
        }
    }
}

struct ToolPaletteBar: View {
    let palette: [Color]
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
            ToolItem(tint: .dangerRed, isSelected: isCrossMode, label: "Çarpı", action: onCrossSelected) {
                Image(systemName: "xmark")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundColor(.dangerRed)
            }
            ForEach(Array(palette.enumerated()), id: \.offset) { index, color in
                let colorIndex = index + 1
                ToolItem(
                    tint: color, isSelected: !isCrossMode && selectedColorIndex == colorIndex,
                    label: "Renk \(colorIndex)", action: { onColorSelected(colorIndex) }
                ) {
                    RoundedRectangle(cornerRadius: 4).fill(color).frame(width: 24, height: 24)
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
