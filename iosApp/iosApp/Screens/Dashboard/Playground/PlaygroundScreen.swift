import Shared
import SwiftUI

struct PlaygroundScreen: View {
    let onNavigateToGame: (String) -> Void
    let onNavigateToSettings: () -> Void
    let onNavigateToTutorial: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.playgroundViewModel() }, state: \.uiState)

    var body: some View {
        PlaygroundContent(
            state: model.state,
            onNavigateToGame: onNavigateToGame,
            onNavigateToSettings: onNavigateToSettings,
            onNavigateToTutorial: onNavigateToTutorial
        )
    }
}

private struct PlaygroundContent: View {
    let state: PlaygroundUiState
    let onNavigateToGame: (String) -> Void
    let onNavigateToSettings: () -> Void
    let onNavigateToTutorial: () -> Void

    var body: some View {
        ScreenBackground {
            WatermarkLogo()

            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 20) {
                        PlaygroundHeader(
                            username: state.username,
                            stars: Int(state.stars),
                            crystals: Int(state.crystals),
                            crystalRefillMinutes: Int(state.crystalRefillMinutes),
                            onNavigateToSettings: onNavigateToSettings,
                            onNavigateToTutorial: onNavigateToTutorial
                        )
                        DailyChallengeCard(
                            isSolved: state.isDailySolved,
                            onPlayToday: { onNavigateToGame("daily-today-20") },
                            onPlayPast: { date in onNavigateToGame("daily-\(date)-20") }
                        )
                    }
                    .padding(.horizontal, 18)
                    .padding(.top, 20)
                }
                .scrollBounceBehavior(.basedOnSize)

                VStack(spacing: 20) {
                    LevelProgressCard(
                        currentLevel: Int(state.currentLevelNumber),
                        hasDraft: state.hasDraft,
                        draftSize: state.draftSize,
                        draftProgress: Int(state.draftProgress),
                        onContinue: { onNavigateToGame(state.currentLevelId) }
                    )
                    RandomPuzzleButton(action: { onNavigateToGame("random-10") })
                }
                .padding(.horizontal, 18)
                .padding(.top, 20)
                .padding(.bottom, 32)
            }
        }
    }
}

private struct PlaygroundHeader: View {
    let username: String
    let stars: Int
    let crystals: Int
    let crystalRefillMinutes: Int
    let onNavigateToSettings: () -> Void
    let onNavigateToTutorial: () -> Void

    var body: some View {
        HStack(spacing: 8) {
            HStack(spacing: 12) {
                Text(username.prefix(2).uppercased())
                    .font(.app(14, .bold))
                    .foregroundColor(.neonCyan)
                    .frame(width: 44, height: 44)
                    .background(Color.surfaceDark)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.neonCyan, lineWidth: 2))

                VStack(alignment: .leading, spacing: 1) {
                    Text("Merhaba,")
                        .font(.app(12))
                        .foregroundColor(.textMuted)
                    Text(username)
                        .font(.app(16, .bold))
                        .foregroundColor(.textPrimary)
                        .lineLimit(1)
                        .minimumScaleFactor(0.75)
                }
            }

            Spacer(minLength: 8)

            StatChip(icon: "star.fill", text: "\(stars)", tint: .gameGold, background: 0.13)

            if crystals > 0 {
                StatChip(icon: "sparkles", text: "\(crystals)", tint: .neonCyan, background: 0.10)
            } else {
                StatChip(icon: "timer", text: "\(crystalRefillMinutes)dk", tint: .dangerRed, background: 0.10, bordered: true)
            }

            HeaderIconButton(icon: "questionmark.circle.fill", label: "Nasıl oynanır", action: onNavigateToTutorial)
            HeaderIconButton(icon: "gearshape.fill", label: "Ayarlar", action: onNavigateToSettings)
        }
    }
}

private struct StatChip: View {
    let icon: String
    let text: String
    let tint: Color
    let background: Double
    var bordered = false

    var body: some View {
        HStack(spacing: 5) {
            Image(systemName: icon).font(.system(size: 12, weight: .bold))
            Text(text).font(.app(13, .bold))
        }
        .foregroundColor(tint)
        .padding(.horizontal, 12)
        .padding(.vertical, 6)
        .background(tint.opacity(background))
        .clipShape(Capsule())
        .overlay(Capsule().stroke(tint.opacity(bordered ? 0.30 : 0), lineWidth: 1))
    }
}

private struct HeaderIconButton: View {
    let icon: String
    let label: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: icon)
                .font(.system(size: 19))
                .foregroundColor(.textMuted)
                .frame(width: 22, height: 22)
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
    }
}

private struct WatermarkLogo: View {
    var body: some View {
        Image("Logo")
            .resizable()
            .frame(width: 180, height: 180)
            .opacity(0.08)
            .allowsHitTesting(false)
    }
}

private struct DailyChallengeCard: View {
    let isSolved: Bool
    let onPlayToday: () -> Void
    let onPlayPast: (String) -> Void

    private static let locale = Locale(identifier: "tr_TR")

    private var today: String { Self.format(Date(), "d MMMM") }

    private var pastDates: [(label: String, key: String)] {
        (1...5).compactMap { daysAgo in
            Calendar.current.date(byAdding: .day, value: -daysAgo, to: Date()).map {
                (Self.format($0, "d MMM"), Self.format($0, "yyyy-MM-dd"))
            }
        }
    }

    private static func format(_ date: Date, _ pattern: String) -> String {
        let formatter = DateFormatter()
        formatter.locale = locale
        formatter.dateFormat = pattern
        return formatter.string(from: date)
    }

    var body: some View {
        DashboardCard(borderColor: isSolved ? .borderDark : .neonCyan.opacity(0.35), cornerRadius: 20) {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    HStack(spacing: 10) {
                        Image(systemName: "calendar")
                            .font(.system(size: 20))
                            .foregroundColor(isSolved ? .textMuted : .neonCyan)
                            .frame(width: 46, height: 46)
                            .background(isSolved ? Color.surfaceVariantDark : Color.neonCyan.opacity(0.13))
                            .clipShape(RoundedRectangle(cornerRadius: 12))

                        VStack(alignment: .leading, spacing: 2) {
                            Text("GÜNÜN BULMACASI")
                                .font(.app(11, .medium))
                                .kerning(1)
                                .foregroundColor(isSolved ? .textMuted : .neonCyan)
                            Text(today)
                                .font(.app(18, .bold))
                                .foregroundColor(.textPrimary)
                        }
                    }
                    Spacer()
                    if isSolved {
                        HStack(spacing: 5) {
                            Image(systemName: "checkmark.circle.fill").font(.system(size: 13))
                            Text("Çözüldü").font(.app(13, .medium))
                        }
                        .foregroundColor(.neonCyan)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 10)
                        .background(Color.surfaceVariantDark)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                    } else {
                        NeonPillButton(title: "Oyna", action: onPlayToday)
                    }
                }

                Rectangle().fill(Color.borderDark).frame(height: 0.5)
                    .padding(.top, 16)
                    .padding(.bottom, 14)

                Text("GEÇMİŞ GÜNLER")
                    .font(.app(11))
                    .kerning(1)
                    .foregroundColor(.textMuted)

                HStack(spacing: 7) {
                    ForEach(pastDates, id: \.key) { date in
                        Button {
                            onPlayPast(date.key)
                        } label: {
                            Text(date.label)
                                .font(.app(11, .medium))
                                .foregroundColor(.textSecondary)
                                .lineLimit(1)
                                .minimumScaleFactor(0.8)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 9)
                                .background(Color.surfaceVariantDark)
                                .clipShape(RoundedRectangle(cornerRadius: 10))
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.top, 10)
            }
            .padding(18)
        }
    }
}

private struct LevelProgressCard: View {
    let currentLevel: Int
    let hasDraft: Bool
    let draftSize: String
    let draftProgress: Int
    let onContinue: () -> Void

    var body: some View {
        DashboardCard(borderColor: .neonCyan.opacity(0.22), cornerRadius: 20) {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    VStack(alignment: .leading, spacing: 3) {
                        Text("MEVCUT SEVİYE")
                            .font(.app(11))
                            .kerning(1)
                            .foregroundColor(.textMuted)
                        Text("\(currentLevel)")
                            .font(.app(36, .bold))
                            .foregroundColor(.neonCyan)
                    }
                    Spacer()
                    NeonPillButton(title: hasDraft ? "Devam Et" : "Başla", verticalPadding: 11, action: onContinue)
                }

                if hasDraft {
                    Rectangle().fill(Color.borderDark).frame(height: 0.5)
                        .padding(.vertical, 14)

                    HStack(spacing: 10) {
                        Image(systemName: "pencil")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.purpleAccent)
                        Text("Yarım kalan bölüm: \(draftSize) · %\(draftProgress)")
                            .font(.app(13))
                            .foregroundColor(.textSecondary)
                    }
                }
            }
            .padding(18)
        }
    }
}

private struct RandomPuzzleButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 14) {
                Image(systemName: "shuffle")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundColor(.purpleAccent)
                    .frame(width: 44, height: 44)
                    .background(Color.purpleAccent.opacity(0.12))
                    .clipShape(RoundedRectangle(cornerRadius: 12))

                VStack(alignment: .leading, spacing: 2) {
                    Text("Rastgele Bölüm Oluştur")
                        .font(.app(15, .semibold))
                        .foregroundColor(.textPrimary)
                    Text("İnternetsiz de çalışır")
                        .font(.app(12))
                        .foregroundColor(.textMuted)
                }
                Spacer()
                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(.textMuted)
            }
            .padding(.horizontal, 18)
            .padding(.vertical, 16)
            .background(Color.surfaceDark)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.borderDark, lineWidth: 0.5))
        }
        .buttonStyle(.plain)
    }
}
