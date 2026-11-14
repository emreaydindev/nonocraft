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
    let userGrid: [[Int]]
    let rowClues: [[GridClue]]
    let colClues: [[GridClue]]
    let cursor: (row: Int, col: Int)?
    let onCellTouched: (Int, Int) -> Void

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
        }
        .accessibilityElement()
        .accessibilityLabel("Bulmaca ızgarası, \(size) x \(size)")
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
        let clueBackground = Color.surfaceDark.opacity(0.5)
        let rowStripWidth = CGFloat(layout.maxRowClues) * cell
        let colStripHeight = CGFloat(layout.maxColClues) * cell
        context.fill(Path(CGRect(x: origin.x - rowStripWidth, y: origin.y, width: rowStripWidth, height: board)), with: .color(clueBackground))
        context.fill(Path(CGRect(x: origin.x, y: origin.y - colStripHeight, width: board, height: colStripHeight)), with: .color(clueBackground))

        let clueFont = Font.app(max(cell * 0.5, 8), .bold)
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
