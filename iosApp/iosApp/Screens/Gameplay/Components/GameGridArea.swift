import SwiftUI

struct GridClue {
    let count: Int
    let colorIndex: Int
}

/// The nonogram board: clue strips on the top and left, the playing grid, and the stick cursor.
/// Touching or dragging over cells reports them through `onCellTouched`.
struct GameGridArea: View {
    let size: Int
    let palette: [Color]
    let paletteHex: [String]
    let userGrid: [[Int]]
    let rowClues: [[GridClue]]
    let colClues: [[GridClue]]
    let cursor: (row: Int, col: Int)?
    let onCellTouched: (Int, Int) -> Void
    var cellActions: BoardCellActions? = nil

    @State private var lastTouched: (Int, Int)?

    private struct Layout {
        let cell: CGFloat
        let origin: CGPoint
        let maxRowClues: Int
        let maxColClues: Int
    }

    private func layout(in available: CGSize) -> Layout {
        let maxRowClues = max(rowClues.map(\.count).max() ?? 1, 1)
        let maxColClues = max(colClues.map(\.count).max() ?? 1, 1)
        let cell = min(
            available.width / CGFloat(maxRowClues + size),
            available.height / CGFloat(maxColClues + size)
        )
        let origin = CGPoint(
            x: (available.width - CGFloat(maxRowClues + size) * cell) / 2 + CGFloat(maxRowClues) * cell,
            y: (available.height - CGFloat(maxColClues + size) * cell) / 2 + CGFloat(maxColClues) * cell
        )
        return Layout(cell: cell, origin: origin, maxRowClues: maxRowClues, maxColClues: maxColClues)
    }

    /// Heavier lines every few cells, as on Android.
    private var subGridSize: Int {
        switch size {
        case 6, 9: 3
        case 8, 12: 4
        default: 5
        }
    }

    var body: some View {
        GeometryReader { proxy in
            let layout = layout(in: proxy.size)
            Canvas { context, _ in
                draw(in: &context, layout: layout)
            }
            .contentShape(Rectangle())
            .gesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { value in
                        let col = Int(floor((value.location.x - layout.origin.x) / layout.cell))
                        let row = Int(floor((value.location.y - layout.origin.y) / layout.cell))
                        guard (0..<size).contains(row), (0..<size).contains(col) else { return }
                        // A drag reports each cell once as the finger enters it.
                        if let lastTouched, lastTouched == (row, col) { return }
                        lastTouched = (row, col)
                        onCellTouched(row, col)
                    }
                    .onEnded { _ in lastTouched = nil }
            )
            .accessibilityHidden(true)
            .overlay { accessibilityNodes(layout: layout) }
        }
    }

    /// Invisible VoiceOver elements over the drawn board: one per clue line ("Satır 3: 1, 1, 1") and one per
    /// cell ("Satır 3, sütun 2" with its state). Activating a cell uses the current tool like a touch; the
    /// actions fill with any palette colour, cross an empty cell or clear a cross. They take no touches, so
    /// drawing on the board is unchanged, and VoiceOver reads them top to bottom, each row's clue first.
    private func accessibilityNodes(layout: Layout) -> some View {
        let cell = layout.cell
        let origin = layout.origin
        return ZStack(alignment: .topLeading) {
            ForEach(Array(colClues.enumerated()), id: \.offset) { col, clues in
                let height = CGFloat(layout.maxColClues) * cell
                node
                    .accessibilityLabel(BoardSpeech.clue(isRow: false, index: col, clues: clues, paletteHex: paletteHex))
                    .placed(CGRect(x: origin.x + CGFloat(col) * cell, y: origin.y - height, width: cell, height: height))
            }
            ForEach(Array(userGrid.enumerated()), id: \.offset) { row, values in
                let width = CGFloat(layout.maxRowClues) * cell
                node
                    .accessibilityLabel(BoardSpeech.clue(isRow: true, index: row, clues: rowClues[safe: row] ?? [], paletteHex: paletteHex))
                    .placed(CGRect(x: origin.x - width, y: origin.y + CGFloat(row) * cell, width: width, height: cell))
                ForEach(Array(values.enumerated()), id: \.offset) { col, value in
                    cellNode(row: row, col: col, value: value, layout: layout)
                }
            }
        }
        .allowsHitTesting(false)
        .accessibilityElement(children: .contain)
    }

    private var node: some View {
        Rectangle().fill(.clear).accessibilityElement()
    }

    private func cellNode(row: Int, col: Int, value: Int, layout: Layout) -> some View {
        let isOpen = value <= 0
        let colorCount = max(paletteHex.count, 1)
        return node
        .accessibilityLabel(BoardSpeech.cellPosition(row: row, col: col))
        .accessibilityValue(BoardSpeech.cellState(value, paletteHex: paletteHex))
        // Filled cells are final; empty and crossed ones take moves.
        .accessibilityAddTraits(isOpen ? .isButton : [])
        .accessibilityAction {
            if isOpen { onCellTouched(row, col) }
        }
        // SwiftUI hands these to VoiceOver last-declared first, so they are declared in reverse to read
        // "Doldur" (each colour in palette order), then "Çarpı koy" or "Temizle", as on Android.
        .accessibilityActions {
            if isOpen, let cellActions {
                if value == 0 {
                    Button("Çarpı koy") { cellActions.cross(row, col) }
                } else {
                    Button("Temizle") { cellActions.clear(row, col) }
                }
                ForEach(Array((1...colorCount).reversed()), id: \.self) { colorIndex in
                    Button(colorCount > 1 ? "Doldur: \(BoardSpeech.colorLabel(colorIndex, paletteHex: paletteHex))" : "Doldur") {
                        cellActions.fill(row, col, colorIndex)
                    }
                }
            }
        }
        .placed(CGRect(
            x: layout.origin.x + CGFloat(col) * layout.cell, y: layout.origin.y + CGFloat(row) * layout.cell,
            width: layout.cell, height: layout.cell
        ))
    }

    private func draw(in context: inout GraphicsContext, layout: Layout) {
        let cell = layout.cell
        let origin = layout.origin
        let board = CGFloat(size) * cell

        context.fill(Path(CGRect(x: origin.x, y: origin.y, width: board, height: board)), with: .color(.surfaceDark))

        // Filled and crossed cells
        for (row, values) in userGrid.enumerated() {
            for (col, value) in values.enumerated() {
                let rect = CGRect(x: origin.x + CGFloat(col) * cell, y: origin.y + CGFloat(row) * cell, width: cell, height: cell)
                if value > 0 {
                    context.fill(Path(rect), with: .color(palette[safe: value - 1] ?? .neonCyan))
                } else if value == -1 {
                    let inset = rect.insetBy(dx: cell * 0.25, dy: cell * 0.25)
                    var cross = Path()
                    cross.move(to: CGPoint(x: inset.minX, y: inset.minY))
                    cross.addLine(to: CGPoint(x: inset.maxX, y: inset.maxY))
                    cross.move(to: CGPoint(x: inset.maxX, y: inset.minY))
                    cross.addLine(to: CGPoint(x: inset.minX, y: inset.maxY))
                    context.stroke(cross, with: .color(.dangerRed), lineWidth: cell * 0.1)
                }
            }
        }

        // Grid lines
        for index in 0...size {
            let isThick = index % subGridSize == 0
            let offset = CGFloat(index) * cell
            var lines = Path()
            lines.move(to: CGPoint(x: origin.x + offset, y: origin.y))
            lines.addLine(to: CGPoint(x: origin.x + offset, y: origin.y + board))
            lines.move(to: CGPoint(x: origin.x, y: origin.y + offset))
            lines.addLine(to: CGPoint(x: origin.x + board, y: origin.y + offset))
            context.stroke(
                lines,
                with: .color(isThick ? .textPrimary.opacity(0.5) : .borderDark),
                lineWidth: isThick ? 3 : 1
            )
        }

        // Clue strips
        // Opaque so clues stay high-contrast over the patterned background (same tone as before).
        let clueBackground = Color(hex: 0x171717)
        let rowStripWidth = CGFloat(layout.maxRowClues) * cell
        let colStripHeight = CGFloat(layout.maxColClues) * cell
        context.fill(Path(CGRect(x: origin.x - rowStripWidth, y: origin.y, width: rowStripWidth, height: board)), with: .color(clueBackground))
        context.fill(Path(CGRect(x: origin.x, y: origin.y - colStripHeight, width: board, height: colStripHeight)), with: .color(clueBackground))

        let clueFont = Font.appFixed(max(cell * 0.5, 8), .bold)
        func drawClue(_ clue: GridClue, in rect: CGRect) {
            guard clue.count > 0 else { return }
            let text = Text("\(clue.count)").font(clueFont).foregroundColor(palette[safe: clue.colorIndex - 1] ?? .neonCyan)
            context.draw(text, at: CGPoint(x: rect.midX, y: rect.midY), anchor: .center)
        }
        for (row, clues) in rowClues.enumerated() {
            for (index, clue) in clues.enumerated() {
                let slotsFromGrid = clues.count - index
                drawClue(clue, in: CGRect(
                    x: origin.x - CGFloat(slotsFromGrid) * cell, y: origin.y + CGFloat(row) * cell, width: cell, height: cell
                ))
            }
        }
        for (col, clues) in colClues.enumerated() {
            for (index, clue) in clues.enumerated() {
                let slotsFromGrid = clues.count - index
                drawClue(clue, in: CGRect(
                    x: origin.x + CGFloat(col) * cell, y: origin.y - CGFloat(slotsFromGrid) * cell, width: cell, height: cell
                ))
            }
        }

        // Stick cursor
        if let cursor {
            let rect = CGRect(x: origin.x + CGFloat(cursor.col) * cell, y: origin.y + CGFloat(cursor.row) * cell, width: cell, height: cell)
            context.fill(Path(rect), with: .color(.neonCyan.opacity(0.2)))
            context.stroke(Path(rect), with: .color(.neonCyan), lineWidth: 3)
        }
    }
}

private extension View {
    /// Sizes and places an accessibility node at a rectangle of the board (layout, so VoiceOver frames follow).
    func placed(_ rect: CGRect) -> some View {
        frame(width: max(rect.width, 1), height: max(rect.height, 1)).position(x: rect.midX, y: rect.midY)
    }
}
