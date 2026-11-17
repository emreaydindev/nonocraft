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

/// Same content as the Android home (wallet, daily, campaign level + draft, random generator),
/// in the same structure, with native controls.
private struct PlaygroundContent: View {
    let state: PlaygroundUiState
    let onNavigateToGame: (String) -> Void
    let onNavigateToSettings: () -> Void
    let onNavigateToTutorial: () -> Void

    /// Scroll view height between the navigation bar and the tab bar.
    @State private var visibleHeight: CGFloat = 0

    var body: some View {
        ScrollView {
            VStack(spacing: 14) {
                WalletStrip(
                    stars: Int(state.stars),
                    crystals: Int(state.crystals),
                    crystalRefillMinutes: Int(state.crystalRefillMinutes)
                )
                DailyCard(
                    isSolved: state.isDailySolved,
                    onPlayToday: { onNavigateToGame("daily-today-20") },
                    onPlayPast: { date in onNavigateToGame("daily-\(date)-20") }
                )
                // Like Android: the mascot fills the gap and the level card and random row sit at the bottom.
                Color.clear
                    .frame(maxWidth: .infinity, minHeight: 24, maxHeight: .infinity)
                    .overlay { WatermarkLogo() }
                LevelProgressCard(
                    currentLevel: Int(state.currentLevelNumber),
                    hasDraft: state.hasDraft,
                    draftSize: state.draftSize,
                    draftProgress: Int(state.draftProgress),
                    onContinue: { onNavigateToGame(state.currentLevelId) }
                )
                RandomPuzzleRow(action: { onNavigateToGame("random-10") })
            }
            .padding(.horizontal, 18)
            .padding(.top, 4)
            .padding(.bottom, 16)
            .frame(maxWidth: 640)
            .frame(maxWidth: .infinity, minHeight: visibleHeight)
        }
        .onScrollGeometryChange(for: CGFloat.self) { geometry in
            geometry.containerSize.height
        } action: { _, height in
            visibleHeight = height
        }
        .background { ScreenBackground { EmptyView() } }
        // The title still names the page for VoiceOver and the back-button menu; the wordmark replaces it on screen.
        .navigationTitle("Oyun Alanı")
        .toolbarTitleDisplayMode(.inline)
        .toolbar(removing: .title)
        .toolbar {
            ToolbarItem(placement: .topBarLeading) {
                BrandName(size: 24)
                    .fixedSize()
                .accessibilityElement(children: .ignore)
                .accessibilityLabel("Nonocraft")
                .accessibilityAddTraits(.isHeader)
            }
            .appGlassBackgroundHidden()
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button("Nasıl oynanır", systemImage: "questionmark.circle", action: onNavigateToTutorial)
                Button("Ayarlar", systemImage: "gearshape", action: onNavigateToSettings)
            }
        }
    }
}

// MARK: - Wallet

/// Ranking stars and the crystal fail pool on one slim row, in place of the Android greeting row.
private struct WalletStrip: View {
    let stars: Int
    let crystals: Int
    let crystalRefillMinutes: Int

    /// Crystal cap for free players (DECISIONS §7); the server stays authoritative.
    private let crystalCap = 5

    var body: some View {
        DashboardCard(cornerRadius: 18) {
            HStack(spacing: 14) {
                HStack(spacing: 6) {
                    Image(systemName: "star.fill")
                        .foregroundStyle(Color.gameGold)
                    Text("\(stars)")
                        .font(.app(17, .bold))
                        .foregroundStyle(Color.textPrimary)
                        .contentTransition(.numericText())
                }
                .accessibilityElement(children: .ignore)
                .accessibilityLabel("\(stars) yıldız")

                Rectangle()
                    .fill(Color.borderDark)
                    .frame(width: 1, height: 22)

                HStack(spacing: 8) {
                    Image(systemName: "diamond.fill")
                        .foregroundStyle(Color.neonCyan)
                    Text("\(crystals)/\(crystalCap)")
                        .font(.app(17, .bold))
                        .foregroundStyle(Color.textPrimary)
                        .contentTransition(.numericText())
                    HStack(spacing: 3) {
                        ForEach(0..<crystalCap, id: \.self) { index in
                            Capsule()
                                .fill(index < crystals ? Color.neonCyan : Color.surfaceVariantDark)
                                .frame(width: 10, height: 5)
                        }
                    }
                }
                .accessibilityElement(children: .ignore)
                .accessibilityLabel("\(crystals) / \(crystalCap) kristal")

                Spacer(minLength: 0)

                if crystals < crystalCap {
                    Label("\(crystalRefillMinutes) dk", systemImage: "timer")
                        .labelStyle(.titleAndIcon)
                        .font(.footnote.weight(.semibold).monospacedDigit())
                        .foregroundStyle(crystals == 0 ? Color.dangerRed : Color.textSecondary)
                        .fixedSize()
                        .accessibilityLabel("Yeni kristale \(crystalRefillMinutes) dakika")
                }
            }
            .font(.system(size: 14, weight: .bold))
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
        }
    }
}

// MARK: - Daily

private struct DailyCard: View {
    let isSolved: Bool
    let onPlayToday: () -> Void
    let onPlayPast: (String) -> Void

    private static let locale = Locale(identifier: "tr_TR")

    private struct Day: Identifiable {
        let id: String  // yyyy-MM-dd, the daily puzzle key
        let weekday: String
        let number: String
        let isToday: Bool
    }

    /// This week as a calendar row: the six days before today, then today.
    private var week: [Day] {
        (0...6).reversed().compactMap { daysAgo in
            Calendar.current.date(byAdding: .day, value: -daysAgo, to: Date()).map {
                Day(
                    id: Self.format($0, "yyyy-MM-dd"),
                    weekday: daysAgo == 0 ? "Bugün" : Self.format($0, "EEE"),
                    number: Self.format($0, "d"),
                    isToday: daysAgo == 0
                )
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
        DashboardCard(borderColor: isSolved ? .borderDark : .neonCyan.opacity(0.35), cornerRadius: 22) {
            VStack(alignment: .leading, spacing: 16) {
                HStack(spacing: 14) {
                    Image(systemName: "calendar")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundStyle(isSolved ? Color.textMuted : Color.neonCyan)
                        .frame(width: 46, height: 46)
                        .background(
                            isSolved ? Color.surfaceVariantDark : Color.neonCyan.opacity(0.13),
                            in: RoundedRectangle(cornerRadius: 12, style: .continuous)
                        )
                        .accessibilityHidden(true)

                    VStack(alignment: .leading, spacing: 2) {
                        Text("GÜNÜN BULMACASI")
                            .font(.app(11, .medium))
                            .kerning(1)
                            .foregroundStyle(isSolved ? Color.textMuted : Color.neonCyan)
                        Text(Self.format(Date(), "d MMMM"))
                            .font(.app(22, .bold))
                            .foregroundStyle(Color.textPrimary)
                    }
                    .accessibilityElement(children: .combine)

                    Spacer(minLength: 0)

                    if isSolved {
                        Label("Çözüldü", systemImage: "checkmark.seal.fill")
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(Color.neonCyan)
                    } else {
                        CompactPlayButton(title: "Oyna", action: onPlayToday)
                    }
                }

                HStack(spacing: 0) {
                    ForEach(week) { day in
                        Button {
                            day.isToday ? onPlayToday() : onPlayPast(day.id)
                        } label: {
                            VStack(spacing: 6) {
                                Text(day.weekday)
                                    .font(.caption2.weight(.semibold))
                                    .foregroundStyle(day.isToday ? Color.neonCyan : Color.textMuted)
                                    .lineLimit(1)
                                    .fixedSize()
                                Text(day.number)
                                    .font(.app(15, .bold))
                                    .foregroundStyle(day.isToday ? Color.onNeon : Color.textPrimary)
                                    .frame(width: 36, height: 36)
                                    .background(day.isToday ? Color.neonCyan : Color.surfaceVariantDark, in: Circle())
                            }
                            .frame(maxWidth: .infinity)
                            .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                        .accessibilityLabel(day.isToday ? "Bugünün bulmacası" : "\(day.number) \(day.weekday) bulmacası")
                    }
                }
            }
            .padding(18)
        }
    }
}

private struct WatermarkLogo: View {
    var body: some View {
        Image("Logo")
            .resizable()
            .scaledToFit()
            .frame(maxWidth: 180, maxHeight: 180)
            .opacity(0.08)
            .padding(.vertical, 8)
            .allowsHitTesting(false)
            .accessibilityHidden(true)
    }
}

// MARK: - Campaign

private struct LevelProgressCard: View {
    let currentLevel: Int
    let hasDraft: Bool
    let draftSize: String
    let draftProgress: Int
    let onContinue: () -> Void

    var body: some View {
        DashboardCard(borderColor: .neonCyan.opacity(0.22), cornerRadius: 22) {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    VStack(alignment: .leading, spacing: 3) {
                        Text("MEVCUT SEVİYE")
                            .font(.app(11))
                            .kerning(1)
                            .foregroundStyle(Color.textMuted)
                        Text("\(currentLevel)")
                            .font(.app(36, .bold))
                            .foregroundStyle(Color.neonCyan)
                    }
                    .accessibilityElement(children: .ignore)
                    .accessibilityLabel("Mevcut seviye \(currentLevel)")
                    Spacer(minLength: 0)
                    CompactPlayButton(title: hasDraft ? "Devam Et" : "Başla", action: onContinue)
                }

                if hasDraft {
                    Rectangle().fill(Color.borderDark).frame(height: 0.5)
                        .padding(.vertical, 14)
                    Label {
                        Text("Yarım kalan bölüm: \(draftSize) · %\(draftProgress)")
                            .font(.subheadline)
                            .foregroundStyle(Color.textSecondary)
                    } icon: {
                        Image(systemName: "pencil")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundStyle(Color.purpleAccent)
                    }
                }
            }
            .padding(18)
        }
    }
}

// MARK: - Random

private struct RandomPuzzleRow: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 14) {
                Image(systemName: "shuffle")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(Color.purpleAccent)
                    .frame(width: 44, height: 44)
                    .background(Color.purpleAccent.opacity(0.12), in: RoundedRectangle(cornerRadius: 12, style: .continuous))

                VStack(alignment: .leading, spacing: 2) {
                    Text("Rastgele Bölüm Oluştur")
                        .font(.app(15, .semibold))
                        .foregroundStyle(Color.textPrimary)
                    Text("İnternetsiz de çalışır")
                        .font(.footnote)
                        .foregroundStyle(Color.textMuted)
                }
                Spacer()
                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(Color.textMuted)
            }
            .padding(.horizontal, 18)
            .padding(.vertical, 14)
            .background(Color.surfaceDark, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
            .overlay(RoundedRectangle(cornerRadius: 20, style: .continuous).stroke(Color.borderDark, lineWidth: 0.5))
        }
        .buttonStyle(.plain)
    }
}

/// Primary action inside a card: prominent glass tinted neon cyan.
private struct CompactPlayButton: View {
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
