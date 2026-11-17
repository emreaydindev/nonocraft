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
        ScrollView {
            VStack(spacing: 20) {
                Greeting(username: state.username)
                DailyChallengeCard(
                    isSolved: state.isDailySolved,
                    onPlayToday: { onNavigateToGame("daily-today-20") },
                    onPlayPast: { date in onNavigateToGame("daily-\(date)-20") }
                )
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
            .padding(.top, 8)
            .padding(.bottom, 24)
            .frame(maxWidth: 640)
            .frame(maxWidth: .infinity)
        }
        .background {
            ScreenBackground { WatermarkLogo() }
        }
        .navigationTitle("Oyun Alanı")
        .toolbarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                WalletIndicator(
                    stars: Int(state.stars),
                    crystals: Int(state.crystals),
                    crystalRefillMinutes: Int(state.crystalRefillMinutes)
                )
            }
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button("Nasıl oynanır", systemImage: "questionmark.circle", action: onNavigateToTutorial)
                Button("Ayarlar", systemImage: "gearshape", action: onNavigateToSettings)
            }
        }
    }
}

/// Avatar initials and "Merhaba, name" in the display font at the top of the page.
private struct Greeting: View {
    let username: String

    var body: some View {
        HStack(spacing: 12) {
            Text(username.prefix(2).uppercased())
                .font(.app(15, .bold))
                .foregroundStyle(Color.neonCyan)
                .frame(width: 48, height: 48)
                .background(Color.surfaceDark, in: Circle())
                .overlay(Circle().stroke(Color.neonCyan, lineWidth: 2))
                .accessibilityHidden(true)

            VStack(alignment: .leading, spacing: 0) {
                Text("Merhaba,")
                    .font(.subheadline)
                    .foregroundStyle(Color.textSecondary)
                Text(username)
                    .font(.app(24, .bold))
                    .foregroundStyle(Color.textPrimary)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
            }
            Spacer(minLength: 0)
        }
        .accessibilityElement(children: .combine)
    }
}

/// Ranking stars and crystals (or the refill countdown at zero) as one toolbar item.
private struct WalletIndicator: View {
    let stars: Int
    let crystals: Int
    let crystalRefillMinutes: Int

    var body: some View {
        HStack(spacing: 10) {
            Label("\(stars)", systemImage: "star.fill")
                .foregroundStyle(Color.gameGold)
                .accessibilityLabel("\(stars) yıldız")
            if crystals > 0 {
                Label("\(crystals)", systemImage: "diamond.fill")
                    .foregroundStyle(Color.neonCyan)
                    .accessibilityLabel("\(crystals) kristal")
            } else {
                Label("\(crystalRefillMinutes)dk", systemImage: "timer")
                    .foregroundStyle(Color.dangerRed)
                    .accessibilityLabel("Yeni kristale \(crystalRefillMinutes) dakika")
            }
        }
        .labelStyle(.titleAndIcon)
        .font(.subheadline.weight(.bold).monospacedDigit())
        .padding(.horizontal, 6)
        .fixedSize()
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
                            Text("Çözüldü").font(.subheadline.weight(.medium))
                        }
                        .foregroundColor(.neonCyan)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 10)
                        .background(Color.surfaceVariantDark)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                    } else {
                        PlayButton(title: "Oyna", action: onPlayToday)
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
                                .font(.footnote.weight(.medium))
                                .foregroundStyle(Color.textSecondary)
                                .lineLimit(1)
                                .minimumScaleFactor(0.7)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 10)
                                .background(Color.surfaceVariantDark, in: Capsule())
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
                    PlayButton(title: hasDraft ? "Devam Et" : "Başla", action: onContinue)
                }

                if hasDraft {
                    Rectangle().fill(Color.borderDark).frame(height: 0.5)
                        .padding(.vertical, 14)

                    HStack(spacing: 10) {
                        Image(systemName: "pencil")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.purpleAccent)
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Yarım kalan bölüm: \(draftSize) · %\(draftProgress)")
                                .font(.subheadline)
                                .foregroundStyle(Color.textSecondary)
                            ProgressView(value: Double(draftProgress), total: 100)
                                .tint(.purpleAccent)
                        }
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
                        .font(.footnote)
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

/// Primary action inside a dashboard card: prominent glass tinted neon cyan.
private struct PlayButton: View {
    let title: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.app(15, .bold))
                .foregroundStyle(Color.onNeon)
                .padding(.horizontal, 4)
        }
        .buttonStyle(.appGlassProminent)
        .tint(.neonCyan)
    }
}
