import Shared
import SwiftUI

struct GameScreen: View {
    let gameId: String
    let onNavigateBack: () -> Void

    @StateObject private var model: ObservedViewModel<GameViewModel, GameUiState>

    init(gameId: String, onNavigateBack: @escaping () -> Void) {
        self.gameId = gameId
        self.onNavigateBack = onNavigateBack
        _model = StateObject(wrappedValue: ObservedViewModel({ $0.gameViewModel(gameId: gameId) }, state: \.state))
    }

    private var state: GameUiState { model.state }
    private var viewModel: GameViewModel { model.viewModel }

    private var paletteHex: [String] { state.puzzle?.paletteHex ?? [] }

    private var palette: [Color] { paletteHex.map { Color(hexString: $0) } }

    private var userGrid: [[Int]] { state.userGrid.map { $0.map(\.intValue) } }

    @FocusState private var isKeyboardFocused: Bool
    private let colorPatterns = ColorPatternsReader()

    var body: some View {
        ScreenBackground {
            if state.isLoading {
                ProgressView().tint(.neonCyan)
            } else if let error = state.error?.message(.game) {
                VStack(spacing: 16) {
                    Text(error)
                        .font(.app(15))
                        .foregroundColor(.dangerRed)
                        .multilineTextAlignment(.center)
                    GhostAuthButton(text: L10n.string("common_go_back"), action: onNavigateBack)
                        .frame(width: 180)
                }
                .padding(24)
            } else if let puzzle = state.puzzle {
                content(puzzle)
            }
        }
        .overlay {
            if state.isVictory {
                GameEndOverlay(
                    title: L10n.string("game_congrats"), subtitle: L10n.string("game_congrats_subtitle"),
                    buttonText: L10n.string("common_continue"), isVictory: true, action: onNavigateBack
                )
            } else if state.isGameOver {
                GameEndOverlay(
                    title: L10n.string("game_over"), subtitle: L10n.string("game_over_subtitle"),
                    buttonText: L10n.string("game_try_again"), isVictory: false, action: onNavigateBack
                )
            }
        }
        // VoiceOver hears a lost heart, the win and game over as they happen.
        .onChange(of: state.currentLives) { old, new in
            if new < old && new > 0 { announce(L10n.format("game_wrong_move", Int(new))) }
        }
        .onChange(of: state.isVictory) { _, isVictory in
            if isVictory { announce(L10n.string("game_victory_announcement")) }
        }
        .onChange(of: state.isGameOver) { _, isGameOver in
            if isGameOver { announce(L10n.string("game_over_announcement")) }
        }
        .navigationTitle(state.puzzle?.title ?? "")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            if let puzzle = state.puzzle, !isFinished {
                ToolbarItem(placement: .principal) {
                    GameTitle(
                        title: puzzle.title,
                        timeLimitSeconds: puzzle.timeLimitSeconds?.int64Value,
                        timeElapsed: state.timeElapsed
                    )
                }
                ToolbarItem(placement: .topBarTrailing) {
                    HeartsIndicator(currentLives: Int(state.currentLives), maxLives: Int(puzzle.maxLives))
                }
            }
        }
        // The end-of-game overlay owns the screen; its button leaves the game.
        .toolbar(isFinished ? .hidden : .automatic, for: .navigationBar)
        .navigationBarBackButtonHidden(isFinished)
        .task {
            let heavy = UIImpactFeedbackGenerator(style: .heavy)
            let tick = UISelectionFeedbackGenerator()
            for await event in viewModel.event {
                switch onEnum(of: event) {
                case .triggerErrorHaptic: heavy.impactOccurred()
                case .triggerTickHaptic: tick.selectionChanged()
                // Sounds and the victory animation are not implemented on Android either.
                case .playErrorSound, .playCorrectSound, .playVictoryAnimation: break
                }
            }
        }
    }

    private var isFinished: Bool { state.isVictory || state.isGameOver }

    /// Board above the controls on phones and in portrait; board and controls side by side when the window
    /// is clearly wider than tall (iPad landscape, wide multitasking windows). Decided from the space the
    /// screen actually gets, not the device, and `AnyLayout` keeps the board's identity when it switches,
    /// so a resize mid-game only rescales it.
    private func content(_ puzzle: Puzzle) -> some View {
        GeometryReader { proxy in
            let isSideBySide = proxy.size.width >= 700 && proxy.size.width > proxy.size.height * 1.15
            let layout = isSideBySide ? AnyLayout(HStackLayout(spacing: 0)) : AnyLayout(VStackLayout(spacing: 0))
            layout {
                GameGridArea(
                    size: Int(puzzle.size),
                    palette: palette,
                    paletteHex: paletteHex,
                    userGrid: userGrid,
                    rowClues: state.rowClues.map { $0.map(GridClue.init) },
                    colClues: state.colClues.map { $0.map(GridClue.init) },
                    cursor: state.isCursorVisible ? (Int(state.cursorRow), Int(state.cursorCol)) : nil,
                    onCellTouched: { row, col in viewModel.onCellClicked(row: Int32(row), col: Int32(col)) },
                    cellActions: cellActions,
                    showColorPatterns: colorPatterns.isEnabled
                )
                // Less margin on small screens (iPhone Duo outer screen) so the clues get the room.
                .padding(min(proxy.size.width, proxy.size.height) < 420 ? 8 : 16)
                .frame(maxWidth: .infinity, maxHeight: .infinity)

                controlBar
                    .frame(width: isSideBySide ? min(420, proxy.size.width * 0.38) : nil)
                    .frame(maxHeight: isSideBySide ? .infinity : nil)
            }
        }
        // Hardware keyboard play (iPad), as on Android: arrows or WASD move, space or return places,
        // X picks the cross, digits pick a colour, H uses a hint, delete clears a cross.
        .focusable()
        .focusEffectDisabled()
        .focused($isKeyboardFocused)
        .onAppear { isKeyboardFocused = true }
        .onKeyPress(phases: [.down, .up], action: handleKey)
    }

    private func handleKey(_ press: KeyPress) -> KeyPress.Result {
        let isDown = press.phase == .down
        let move: (Int, Int)? = switch press.key {
        case .upArrow, "w": (-1, 0)
        case .downArrow, "s": (1, 0)
        case .leftArrow, "a": (0, -1)
        case .rightArrow, "d": (0, 1)
        default: nil
        }
        if let move {
            if isDown {
                viewModel.setAnalogFocus(isFocused: true)
                viewModel.moveCursor(dRow: Int32(move.0), dCol: Int32(move.1))
            } else {
                viewModel.setAnalogFocus(isFocused: false)
            }
            return .handled
        }
        switch press.key {
        case .space, .return:
            if isDown {
                viewModel.setAnalogFocus(isFocused: true)
                viewModel.setPlacePressed(isPressed: true)
            } else {
                viewModel.setPlacePressed(isPressed: false)
                viewModel.setAnalogFocus(isFocused: false)
            }
            return .handled
        case "x":
            if isDown { viewModel.toggleCrossMode(isCross: true) }
            return .handled
        case .delete, .deleteForward:
            if isDown { cellActions.clear(Int(state.cursorRow), Int(state.cursorCol)) }
            return .handled
        case "h":
            if isDown { viewModel.useHint() }
            return .handled
        default:
            if let digit = Int(press.characters), (1...paletteHex.count).contains(digit) {
                if isDown { viewModel.selectColor(colorIndex: Int32(digit)) }
                return .handled
            }
            return .ignored
        }
    }

    /// Single-cell moves for VoiceOver and the keyboard, made from the view model's own moves (pick a tool,
    /// touch the cell); the player's tool is put back afterwards.
    private var cellActions: BoardCellActions {
        let viewModel = viewModel
        func value(_ row: Int, _ col: Int) -> Int? {
            let grid = viewModel.state.value.userGrid
            guard grid.indices.contains(row), grid[row].indices.contains(col) else { return nil }
            return grid[row][col].intValue
        }
        func withCrossTool(_ move: () -> Void) {
            let wasCrossMode = viewModel.state.value.isCrossMode
            viewModel.toggleCrossMode(isCross: true)
            move()
            if !wasCrossMode { viewModel.toggleCrossMode(isCross: false) }
        }
        return BoardCellActions(
            fill: { row, col, colorIndex in
                let before = viewModel.state.value
                viewModel.selectColor(colorIndex: Int32(colorIndex))
                viewModel.onCellClicked(row: Int32(row), col: Int32(col))
                if before.isCrossMode {
                    viewModel.toggleCrossMode(isCross: true)
                } else if Int(before.selectedColorIndex) != colorIndex {
                    viewModel.selectColor(colorIndex: before.selectedColorIndex)
                }
            },
            cross: { row, col in
                if value(row, col) == 0 { withCrossTool { viewModel.onCellClicked(row: Int32(row), col: Int32(col)) } }
            },
            clear: { row, col in
                if value(row, col) == -1 { withCrossTool { viewModel.onCellClicked(row: Int32(row), col: Int32(col)) } }
            }
        )
    }

    private func cursorDescription(row: Int, col: Int) -> String {
        BoardSpeech.cellPosition(row: row, col: col) + ", "
            + BoardSpeech.cellState(userGrid[safe: row]?[safe: col] ?? 0, paletteHex: paletteHex)
    }

    /// Moves the cursor for VoiceOver and says where it landed (custom actions do not re-read the value).
    private func accessibilityMove(dRow: Int, dCol: Int) {
        let size = Int(state.puzzle?.size ?? 0)
        guard size > 0 else { return }
        let row = min(max(Int(state.cursorRow) + dRow, 0), size - 1)
        let col = min(max(Int(state.cursorCol) + dCol, 0), size - 1)
        viewModel.setAnalogFocus(isFocused: true)
        viewModel.moveCursor(dRow: Int32(dRow), dCol: Int32(dCol))
        viewModel.setAnalogFocus(isFocused: false)
        announce(cursorDescription(row: row, col: col))
    }

    /// Next or previous cell in reading order, wrapping to the next or previous row.
    private func accessibilityStep(forward: Bool) {
        let size = Int(state.puzzle?.size ?? 0)
        let col = Int(state.cursorCol)
        if forward {
            col < size - 1 ? accessibilityMove(dRow: 0, dCol: 1) : accessibilityMove(dRow: 1, dCol: -(size - 1))
        } else {
            col > 0 ? accessibilityMove(dRow: 0, dCol: -1) : accessibilityMove(dRow: -1, dCol: size - 1)
        }
    }

    private func announce(_ text: String) {
        AccessibilityNotification.Announcement(text).post()
    }

    private var controlBar: some View {
        GameControlBar(
            palette: palette,
            colorLabels: paletteHex.indices.map { index in
                paletteHex.count > 1 ? BoardSpeech.colorLabel(index + 1, paletteHex: paletteHex).capitalizedFirst : L10n.string("game_fill")
            },
            showColorPatterns: colorPatterns.isEnabled,
            selectedColorIndex: Int(state.selectedColorIndex),
            isCrossMode: state.isCrossMode,
            hintsRemaining: Int(state.hintsRemaining),
            cursorDescription: cursorDescription(row: Int(state.cursorRow), col: Int(state.cursorCol)),
            onMove: { dRow, dCol in viewModel.moveCursor(dRow: Int32(dRow), dCol: Int32(dCol)) },
            onAccessibilityMove: { dRow, dCol in accessibilityMove(dRow: dRow, dCol: dCol) },
            onAccessibilityStep: { accessibilityStep(forward: $0) },
            onStickFocusChange: { viewModel.setAnalogFocus(isFocused: $0) },
            onPlacePressed: { viewModel.setPlacePressed(isPressed: $0) },
            onHint: { viewModel.useHint() },
            onColorSelected: { viewModel.selectColor(colorIndex: Int32($0)) },
            onCrossSelected: { viewModel.toggleCrossMode(isCross: true) }
        )
    }
}

extension GridClue {
    init(_ clue: Clue) {
        self.init(count: Int(clue.count), colorIndex: Int(clue.colorIndex))
    }
}

private extension String {
    var capitalizedFirst: String { prefix(1).uppercased() + dropFirst() }
}

extension Array {
    subscript(safe index: Int) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}

/// Toolbar title: puzzle name in the display font with the clock underneath.
/// Counts down in red when the puzzle has a time limit, otherwise shows the time spent.
private struct GameTitle: View {
    let title: String
    let timeLimitSeconds: Int64?
    let timeElapsed: Int64

    private var seconds: Int64 {
        timeLimitSeconds.map { max(0, $0 - timeElapsed) } ?? timeElapsed
    }

    var body: some View {
        VStack(spacing: 0) {
            Text(title)
                .font(.app(17, .bold))
                .kerning(0.5)
                .foregroundStyle(Color.textPrimary)
                .lineLimit(1)
            Label(String(format: "%02d:%02d", seconds / 60, seconds % 60), systemImage: timeLimitSeconds == nil ? "clock" : "timer")
                .labelStyle(.titleAndIcon)
                .font(.caption.monospacedDigit().weight(.semibold))
                .foregroundStyle(timeLimitSeconds == nil ? Color.textSecondary : Color.dangerRed)
                .contentTransition(.numericText(countsDown: timeLimitSeconds != nil))
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel(title)
        .accessibilityValue(L10n.format(
            timeLimitSeconds == nil ? "game_time_elapsed" : "game_time_remaining",
            L10n.format("game_minutes", Int(seconds / 60)) + " " + L10n.format("game_seconds", Int(seconds % 60))
        ))
        .accessibilityAddTraits(.isHeader)
    }
}

private struct HeartsIndicator: View {
    let currentLives: Int
    let maxLives: Int

    var body: some View {
        HStack(spacing: 2) {
            ForEach(0..<max(maxLives, 0), id: \.self) { index in
                let hasLife = index < currentLives
                Image(systemName: hasLife ? "heart.fill" : "heart")
                    .font(.system(size: maxLives > 3 ? 13 : 15, weight: .semibold))
                    .foregroundStyle(hasLife ? Color.dangerRed : Color.dangerRed.opacity(0.35))
                    .symbolEffect(.bounce, value: hasLife)
            }
        }
        .padding(.horizontal, 4)
        .frame(minHeight: 44)
        .contentShape(Rectangle())
        .accessibilityElement()
        .accessibilityLabel(L10n.format("game_lives_left", Int(currentLives)))
    }
}

private struct GameEndOverlay: View {
    let title: String
    let subtitle: String
    let buttonText: String
    let isVictory: Bool
    let action: () -> Void

    var body: some View {
        ZStack {
            Color.black.opacity(0.85).ignoresSafeArea()
            VStack(spacing: 0) {
                Text(title)
                    .font(.app(36, .bold))
                    .kerning(4)
                    .foregroundColor(isVictory ? .neonCyan : .dangerRed)
                    .multilineTextAlignment(.center)
                    .accessibilityAddTraits(.isHeader)
                Text(subtitle)
                    .font(.app(16))
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.top, 12)
                Button(action: action) {
                    Text(buttonText)
                        .font(.app(16, .bold))
                        .foregroundColor(isVictory ? .backgroundDark : .textPrimary)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 12)
                        .frame(minWidth: 200, minHeight: 56)
                        .background(isVictory ? Color.neonCyan : Color.surfaceDark)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                }
                .buttonStyle(.plain)
                .padding(.top, 48)
            }
            .padding(24)
        }
        // VoiceOver stays inside the overlay; the board behind it is finished.
        .accessibilityElement(children: .contain)
        .accessibilityAddTraits(.isModal)
        .transition(.opacity)
    }
}
