import SwiftUI

/// Four-step interactive tutorial: drag to fill, mark an empty cell, fill coloured groups, done.
struct TutorialScreen: View {
    let onNavigateToDashboard: () -> Void

    private enum Tool { case fill, cross, purple, gold }
    fileprivate enum CellState { case empty, cyan, purple, gold, cross }

    @State private var page = 0
    @State private var selectedTool: Tool = .fill
    @State private var grid1: [CellState] = Array(repeating: .empty, count: 4)
    @State private var grid2: [CellState] = [.cyan, .empty, .cyan, .cyan, .empty]
    @State private var grid3: [CellState] = Array(repeating: .empty, count: 5)

    private let titles = ["Satırları Doldur", "Boş Kareler", "Renkli Gruplar", "Tebrikler"]
    private let descriptions = [
        "Soldaki sayı o satırda kaç kareyi doldurman gerektiğini belirtir. İlk satırı doldurmak için sürükle.",
        "Kesin boş olduğunu bildiğin kareleri çarpı ile işaretle. Boşluğu işaretlemek için çarpı aracını seç.",
        "Renkli bulmacalarda farklı renkler yan yana gelebilir. İstenen renkleri kullanarak hücreleri doldur.",
        "Tüm temel kuralları öğrendin. Artık Nonocraft dünyasına girmeye hazırsın.",
    ]
    private let successGreen = Color(hex: 0x4CAF50)
    private let wrongToolHaptic = UINotificationFeedbackGenerator()

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {
                header
                stepText
                    .frame(height: 130, alignment: .top)

                stepContent
                    .frame(maxWidth: .infinity, maxHeight: .infinity)

                if page == 3 {
                    Button(action: onNavigateToDashboard) {
                        Text("Başla")
                            .font(.app(15, .semibold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 52)
                            .background(successGreen)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .buttonStyle(.plain)
                    .padding(24)
                } else {
                    toolRow
                        .padding(.bottom, 50)
                }
            }
        }
        .toolbar(.hidden, for: .navigationBar)
    }

    private var header: some View {
        HStack {
            HStack(spacing: 6) {
                ForEach(0..<4, id: \.self) { index in
                    Circle()
                        .fill(index == page ? Color.neonCyan : Color.surfaceVariantDark)
                        .frame(width: index == page ? 8 : 6, height: index == page ? 8 : 6)
                }
            }
            .accessibilityHidden(true)
            Spacer()
            Button(action: onNavigateToDashboard) {
                Text("Atla")
                    .font(.app(12))
                    .foregroundColor(.textSecondary)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 7)
                    .background(Color.surfaceDark)
                    .clipShape(Capsule())
                    .overlay(Capsule().stroke(Color.borderDark, lineWidth: 0.5))
            }
            .buttonStyle(.plain)
            .opacity(page == 3 ? 0 : 1)
            .disabled(page == 3)
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 14)
    }

    private var stepText: some View {
        VStack(spacing: 0) {
            Text("ADIM \(page + 1) / 4")
                .font(.app(11))
                .kerning(1.5)
                .foregroundColor(.neonCyan.opacity(0.7))
            Text(titles[page])
                .font(.app(20, .bold))
                .foregroundColor(.textPrimary)
                .padding(.top, 2)
            Text(descriptions[page])
                .font(.app(12))
                .lineSpacing(2)
                .foregroundColor(.textSecondary)
                .multilineTextAlignment(.center)
                .padding(.top, 6)
        }
        .padding(.horizontal, 24)
        .id(page)
        .transition(.opacity)
    }

    @ViewBuilder
    private var stepContent: some View {
        switch page {
        case 0:
            HStack(spacing: 16) {
                Text("4").font(.app(24, .bold)).foregroundColor(.textPrimary)
                DragFillRow(cells: grid1, handStartCell: 0, showsHand: grid1.contains(.empty), onReachNextCell: fillStep1)
            }
        case 1:
            HStack(spacing: 16) {
                VStack(alignment: .trailing, spacing: 0) {
                    Text("1")
                    Text("3")
                }
                .font(.app(18, .bold))
                .foregroundColor(.textPrimary)
                HStack(spacing: 4) {
                    ForEach(grid2.indices, id: \.self) { index in
                        Button { crossStep2(index) } label: { TutorialCell(state: grid2[index]) }
                            .buttonStyle(.plain)
                            .accessibilityLabel("Kare \(index + 1)")
                    }
                }
            }
        case 2:
            let filled = grid3.filter { $0 != .empty }.count
            HStack(spacing: 16) {
                VStack(alignment: .trailing, spacing: 0) {
                    Text("3").foregroundColor(.purpleAccent)
                    Text("2").foregroundColor(.gameGold)
                }
                .font(.app(18, .bold))
                DragFillRow(cells: grid3, handStartCell: filled, showsHand: filled < 5, onReachNextCell: fillStep3)
            }
        default:
            CompletedCheckmark(color: successGreen)
        }
    }

    private var toolRow: some View {
        HStack(spacing: 16) {
            toolButton(.cross, tint: .textPrimary, label: "Çarpı") {
                Text("✕").font(.system(size: 24, weight: .bold)).foregroundColor(.dangerRed)
            }
            toolButton(.fill, tint: .neonCyan, label: "Doldur") { swatch(.neonCyan) }
            if page >= 2 {
                toolButton(.purple, tint: .purpleAccent, label: "Mor") { swatch(.purpleAccent) }
                toolButton(.gold, tint: .gameGold, label: "Altın") { swatch(.gameGold) }
            }
        }
    }

    private func swatch(_ color: Color) -> some View {
        RoundedRectangle(cornerRadius: 4).fill(color).frame(width: 24, height: 24)
    }

    private func toolButton<Content: View>(
        _ tool: Tool, tint: Color, label: String, @ViewBuilder content: () -> Content
    ) -> some View {
        let isSelected = selectedTool == tool
        return Button {
            selectedTool = tool
        } label: {
            content()
                .frame(width: 54, height: 54)
                .background(isSelected && tool != .cross ? tint.opacity(0.2) : .clear)
                .clipShape(Circle())
                .overlay(Circle().stroke(isSelected ? tint : Color.borderDark, lineWidth: 2))
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }

    // MARK: - Step logic (same rules as Android)

    private func fillStep1() {
        guard selectedTool == .fill else { return wrongToolHaptic.notificationOccurred(.error) }
        guard let next = grid1.firstIndex(of: .empty) else { return }
        grid1[next] = .cyan
        if !grid1.contains(.empty) { advance(to: 1, after: 0.6) }
    }

    private func crossStep2(_ index: Int) {
        guard selectedTool == .cross, index == 1 else { return wrongToolHaptic.notificationOccurred(.error) }
        grid2[1] = .cross
        advance(to: 2, after: 0.8)
    }

    private func fillStep3() {
        guard let next = grid3.firstIndex(of: .empty) else { return }
        let required: Tool = next < 3 ? .purple : .gold
        guard selectedTool == required else { return wrongToolHaptic.notificationOccurred(.error) }
        grid3[next] = required == .purple ? .purple : .gold
        if !grid3.contains(.empty) { advance(to: 3, after: 0.6) }
    }

    private func advance(to nextPage: Int, after delay: Double) {
        Task {
            try? await Task.sleep(for: .seconds(delay))
            withAnimation(.easeInOut(duration: 0.3)) { page = nextPage }
        }
    }
}

private struct TutorialCell: View {
    let state: TutorialScreen.CellState

    private var fill: Color {
        switch state {
        case .empty, .cross: .surfaceDark
        case .cyan: .neonCyan
        case .purple: .purpleAccent
        case .gold: .gameGold
        }
    }

    var body: some View {
        RoundedRectangle(cornerRadius: 8)
            .fill(fill)
            .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.borderDark, lineWidth: 0.5))
            .frame(width: 50, height: 50)
            .overlay {
                if state == .cross {
                    Text("✕").font(.system(size: 20, weight: .bold)).foregroundColor(.dangerRed)
                }
            }
    }
}

/// A row of cells filled by dragging across it, with a pointing hand hinting at the next cell.
private struct DragFillRow: View {
    let cells: [TutorialScreen.CellState]
    let handStartCell: Int
    let showsHand: Bool
    let onReachNextCell: () -> Void

    private let cellPitch: CGFloat = 54
    @State private var handTravel: CGFloat = 0

    var body: some View {
        HStack(spacing: 4) {
            ForEach(cells.indices, id: \.self) { index in
                TutorialCell(state: cells[index])
            }
        }
        .contentShape(Rectangle())
        .gesture(
            DragGesture(minimumDistance: 0)
                .onChanged { value in
                    let index = min(max(Int(value.location.x / cellPitch), 0), cells.count - 1)
                    if index == cells.filter({ $0 != .empty }).count { onReachNextCell() }
                }
        )
        .overlay(alignment: .topLeading) {
            if showsHand {
                Text("👆")
                    .font(.system(size: 30))
                    .offset(x: CGFloat(handStartCell) * cellPitch + handTravel + 8, y: 18)
                    .allowsHitTesting(false)
                    .accessibilityHidden(true)
            }
        }
        .task(id: handStartCell) {
            handTravel = 0
            while !Task.isCancelled {
                withAnimation(.easeOut(duration: 0.9)) { handTravel = cellPitch }
                try? await Task.sleep(for: .milliseconds(1300))
                handTravel = 0
                try? await Task.sleep(for: .milliseconds(50))
            }
        }
        .accessibilityElement()
        .accessibilityLabel("Doldurulacak satır")
    }
}

/// Solved nonogram in the shape of a check mark.
private struct CompletedCheckmark: View {
    let color: Color

    private let rows = [
        "00000000000000", "00000000000000", "00000000000100", "00000000001110", "00000000011111",
        "00100000111110", "01110001111100", "11111011111000", "01111111110000", "00111111100000",
        "00011111000000", "00001110000000", "00000100000000", "00000000000000",
    ]
    private let rowClues = ["", "", "1", "3", "5", "1 5", "3 5", "5 5", "9", "7", "5", "3", "1", ""]
    private let colClues = ["1", "3", "5", "5", "5", "5", "5", "5", "5", "5", "5", "5", "3", "1"]

    var body: some View {
        HStack(alignment: .bottom, spacing: 4) {
            VStack(alignment: .trailing, spacing: 1) {
                ForEach(rowClues.indices, id: \.self) { index in
                    Text(rowClues[index])
                        .font(.app(9, .bold))
                        .foregroundColor(color)
                        .frame(height: 18)
                }
            }
            VStack(alignment: .leading, spacing: 2) {
                HStack(spacing: 1) {
                    ForEach(colClues.indices, id: \.self) { index in
                        Text(colClues[index])
                            .font(.app(9, .bold))
                            .foregroundColor(color)
                            .frame(width: 18)
                    }
                }
                VStack(spacing: 1) {
                    ForEach(rows.indices, id: \.self) { row in
                        HStack(spacing: 1) {
                            ForEach(Array(rows[row].enumerated()), id: \.offset) { _, char in
                                RoundedRectangle(cornerRadius: 2)
                                    .fill(char == "1" ? color : Color.surfaceDark)
                                    .frame(width: 18, height: 18)
                            }
                        }
                    }
                }
            }
        }
        .accessibilityElement()
        .accessibilityLabel("Tamamlanmış bulmaca")
    }
}
