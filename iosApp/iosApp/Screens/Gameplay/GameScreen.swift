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

    private var palette: [Color] {
        (state.puzzle?.paletteHex ?? []).map { Color(hexString: $0) }
    }

    var body: some View {
        ScreenBackground {
            if state.isLoading {
                ProgressView().tint(.neonCyan)
            } else if let error = state.errorMessage {
                VStack(spacing: 16) {
                    Text(error)
                        .font(.app(15))
                        .foregroundColor(.dangerRed)
                        .multilineTextAlignment(.center)
                    GhostAuthButton(text: "Geri Dön", action: onNavigateBack)
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
                    title: "TEBRİKLER!", subtitle: "Bulmacayı başarıyla çözdün.",
                    buttonText: "Devam Et", isVictory: true, action: onNavigateBack
                )
            } else if state.isGameOver {
                GameEndOverlay(
                    title: "OYUN BİTTİ", subtitle: "Tüm canların tükendi.",
                    buttonText: "Tekrar Dene", isVictory: false, action: onNavigateBack
                )
            }
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

    private func content(_ puzzle: Puzzle) -> some View {
        VStack(spacing: 0) {
            GameGridArea(
                size: Int(puzzle.size),
                palette: palette,
                userGrid: state.userGrid.map { $0.map(\.intValue) },
                rowClues: state.rowClues.map { $0.map(GridClue.init) },
                colClues: state.colClues.map { $0.map(GridClue.init) },
                cursor: state.isCursorVisible ? (Int(state.cursorRow), Int(state.cursorCol)) : nil,
                onCellTouched: { row, col in viewModel.onCellClicked(row: Int32(row), col: Int32(col)) }
            )
            .padding(16)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        // The board is laid out above the bar, so the glass never covers a cell.
        .safeAreaInset(edge: .bottom, spacing: 0) {
            GameControlBar(
                palette: palette,
                selectedColorIndex: Int(state.selectedColorIndex),
                isCrossMode: state.isCrossMode,
                hintsRemaining: Int(state.hintsRemaining),
                onMove: { dRow, dCol in viewModel.moveCursor(dRow: Int32(dRow), dCol: Int32(dCol)) },
                onStickFocusChange: { viewModel.setAnalogFocus(isFocused: $0) },
                onPlacePressed: { viewModel.setPlacePressed(isPressed: $0) },
                onHint: { viewModel.useHint() },
                onColorSelected: { viewModel.selectColor(colorIndex: Int32($0)) },
                onCrossSelected: { viewModel.toggleCrossMode(isCross: true) }
            )
        }
    }
}

extension GridClue {
    init(_ clue: Clue) {
        self.init(count: Int(clue.count), colorIndex: Int(clue.colorIndex))
    }
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
        .accessibilityElement(children: .combine)
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
        .accessibilityElement()
        .accessibilityLabel("\(currentLives) can kaldı")
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
                Text(subtitle)
                    .font(.app(16))
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.top, 12)
                Button(action: action) {
                    Text(buttonText)
                        .font(.app(16, .bold))
                        .foregroundColor(isVictory ? .backgroundDark : .textPrimary)
                        .frame(width: 200, height: 56)
                        .background(isVictory ? Color.neonCyan : Color.surfaceDark)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                }
                .buttonStyle(.plain)
                .padding(.top, 48)
            }
            .padding(24)
        }
        .transition(.opacity)
    }
}
