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
/// laid out the iOS way: large title with toolbar buttons, a wallet row, a hero daily card.
private struct PlaygroundContent: View {
    let state: PlaygroundUiState
    let onNavigateToGame: (String) -> Void
    let onNavigateToSettings: () -> Void
    let onNavigateToTutorial: () -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                WalletRow(
                    stars: Int(state.stars),
                    crystals: Int(state.crystals),
                    crystalRefillMinutes: Int(state.crystalRefillMinutes)
                )
                DailyHeroCard(
                    isSolved: state.isDailySolved,
                    onPlayToday: { onNavigateToGame("daily-today-20") },
                    onPlayPast: { date in onNavigateToGame("daily-\(date)-20") }
                )
                CampaignCard(
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
            .padding(.bottom, 24)
            .frame(maxWidth: 640)
            .frame(maxWidth: .infinity)
        }
        .background { ScreenBackground { EmptyView() } }
        .navigationTitle("Oyun Alanı")
        .toolbarTitleDisplayMode(.inlineLarge)
        .toolbar {
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button("Nasıl oynanır", systemImage: "questionmark.circle", action: onNavigateToTutorial)
                Button("Ayarlar", systemImage: "gearshape", action: onNavigateToSettings)
            }
        }
    }
}

// MARK: - Wallet

/// Ranking stars and the crystal fail pool side by side, in place of the Android greeting row.
private struct WalletRow: View {
    let stars: Int
    let crystals: Int
    let crystalRefillMinutes: Int

    /// Crystal cap for free players (DECISIONS §7); the server stays authoritative.
    private let crystalCap = 5

    var body: some View {
        HStack(spacing: 12) {
            WalletTile(title: "YILDIZ", systemImage: "star.fill", tint: .gameGold) {
                Text("\(stars)")
                    .font(.app(28, .bold))
                    .foregroundStyle(Color.textPrimary)
                    .contentTransition(.numericText())
            } footer: {
                Text("Sıralama puanı")
                    .font(.footnote)
                    .foregroundStyle(Color.textMuted)
            }
            .accessibilityElement(children: .ignore)
            .accessibilityLabel("\(stars) yıldız")

            WalletTile(title: "KRİSTAL", systemImage: "diamond.fill", tint: .neonCyan) {
                HStack(alignment: .firstTextBaseline, spacing: 2) {
                    Text("\(crystals)")
                        .font(.app(28, .bold))
                        .foregroundStyle(Color.textPrimary)
                        .contentTransition(.numericText())
                    Text("/\(crystalCap)")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(Color.textMuted)
                }
            } footer: {
                HStack(spacing: 8) {
                    HStack(spacing: 3) {
                        ForEach(0..<crystalCap, id: \.self) { index in
                            Capsule()
                                .fill(index < crystals ? Color.neonCyan : Color.surfaceVariantDark)
                                .frame(height: 5)
                        }
                    }
                    if crystals < crystalCap {
                        Label("\(crystalRefillMinutes) dk", systemImage: "timer")
                            .labelStyle(.titleAndIcon)
                            .font(.footnote.weight(.semibold).monospacedDigit())
                            .foregroundStyle(crystals == 0 ? Color.dangerRed : Color.textSecondary)
                            .fixedSize()
                    }
                }
            }
            .accessibilityElement(children: .ignore)
            .accessibilityLabel(crystalAccessibilityLabel)
        }
    }

    private var crystalAccessibilityLabel: String {
        let count = "\(crystals) / \(crystalCap) kristal"
        return crystals < crystalCap ? "\(count), yenisine \(crystalRefillMinutes) dakika" : count
    }
}

private struct WalletTile<Value: View, Footer: View>: View {
    let title: String
    let systemImage: String
    let tint: Color
    @ViewBuilder var value: Value
    @ViewBuilder var footer: Footer

    var body: some View {
        DashboardCard(cornerRadius: 20) {
            VStack(alignment: .leading, spacing: 6) {
                HStack(spacing: 6) {
                    Image(systemName: systemImage)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundStyle(tint)
                    Text(title)
                        .font(.app(11, .medium))
                        .kerning(1)
                        .foregroundStyle(Color.textMuted)
                }
                value
                footer
                    .frame(maxWidth: .infinity, minHeight: 18, alignment: .leading)
            }
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

// MARK: - Daily

private struct DailyHeroCard: View {
    let isSolved: Bool
    let onPlayToday: () -> Void
    let onPlayPast: (String) -> Void

    private static let locale = Locale(identifier: "tr_TR")

    private struct PastDay: Identifiable {
        let id: String  // yyyy-MM-dd, the daily puzzle key
        let weekday: String
        let day: String
    }

    /// The five days before today, oldest first, like a calendar week row.
    private var pastDays: [PastDay] {
        (1...5).reversed().compactMap { daysAgo in
            Calendar.current.date(byAdding: .day, value: -daysAgo, to: Date()).map {
                PastDay(
                    id: Self.format($0, "yyyy-MM-dd"),
                    weekday: Self.format($0, "EEE"),
                    day: Self.format($0, "d")
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
        DashboardCard(borderColor: isSolved ? .borderDark : .neonCyan.opacity(0.35), cornerRadius: 24) {
            VStack(alignment: .leading, spacing: 18) {
                HStack(alignment: .center, spacing: 12) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("GÜNÜN BULMACASI")
                            .font(.app(11, .medium))
                            .kerning(1)
                            .foregroundStyle(isSolved ? Color.textMuted : Color.neonCyan)
                        Text(Self.format(Date(), "d MMMM"))
                            .font(.app(30, .bold))
                            .foregroundStyle(Color.textPrimary)
                        Text(Self.format(Date(), "EEEE"))
                            .font(.subheadline)
                            .foregroundStyle(Color.textSecondary)
                    }
                    Spacer(minLength: 0)
                    Image("Logo")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 88, height: 88)
                        .shadow(color: .neonCyan.opacity(isSolved ? 0 : 0.45), radius: 18)
                        .saturation(isSolved ? 0.2 : 1)
                        .accessibilityHidden(true)
                }

                if isSolved {
                    Label("Bugünün bulmacası çözüldü", systemImage: "checkmark.circle.fill")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(Color.neonCyan)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                        .background(Color.surfaceVariantDark, in: Capsule())
                } else {
                    PlayButton(title: "Oyna", systemImage: "play.fill", action: onPlayToday)
                }

                VStack(alignment: .leading, spacing: 10) {
                    Text("GEÇMİŞ GÜNLER")
                        .font(.app(11))
                        .kerning(1)
                        .foregroundStyle(Color.textMuted)
                    HStack(spacing: 0) {
                        ForEach(pastDays) { day in
                            Button {
                                onPlayPast(day.id)
                            } label: {
                                VStack(spacing: 6) {
                                    Text(day.weekday)
                                        .font(.caption2.weight(.semibold))
                                        .foregroundStyle(Color.textMuted)
                                    Text(day.day)
                                        .font(.app(16, .bold))
                                        .foregroundStyle(Color.textPrimary)
                                        .frame(width: 40, height: 40)
                                        .background(Color.surfaceVariantDark, in: Circle())
                                }
                                .frame(maxWidth: .infinity)
                                .contentShape(Rectangle())
                            }
                            .buttonStyle(.plain)
                            .accessibilityLabel("\(day.day) \(day.weekday) bulmacasını oyna")
                        }
                    }
                }
            }
            .padding(20)
        }
    }
}

// MARK: - Campaign

private struct CampaignCard: View {
    let currentLevel: Int
    let hasDraft: Bool
    let draftSize: String
    let draftProgress: Int
    let onContinue: () -> Void

    var body: some View {
        DashboardCard(borderColor: .neonCyan.opacity(0.22), cornerRadius: 24) {
            VStack(spacing: 18) {
                HStack(spacing: 14) {
                    Text("\(currentLevel)")
                        .font(.app(30, .bold))
                        .foregroundStyle(Color.neonCyan)
                        .frame(width: 64, height: 64)
                        .background(Color.neonCyan.opacity(0.12), in: RoundedRectangle(cornerRadius: 18, style: .continuous))
                        .accessibilityHidden(true)

                    VStack(alignment: .leading, spacing: 3) {
                        Text("MEVCUT SEVİYE")
                            .font(.app(11))
                            .kerning(1)
                            .foregroundStyle(Color.textMuted)
                        Text("Seviye \(currentLevel)")
                            .font(.app(20, .bold))
                            .foregroundStyle(Color.textPrimary)
                        if hasDraft {
                            Text("Yarım kalan bölüm · \(draftSize)")
                                .font(.footnote)
                                .foregroundStyle(Color.textSecondary)
                        }
                    }
                    .accessibilityElement(children: .combine)

                    Spacer(minLength: 0)

                    if hasDraft {
                        Gauge(value: Double(draftProgress), in: 0...100) {
                            EmptyView()
                        } currentValueLabel: {
                            Text("%\(draftProgress)")
                                .font(.caption2.weight(.bold))
                                .foregroundStyle(Color.textPrimary)
                        }
                        .gaugeStyle(.accessoryCircularCapacity)
                        .tint(.purpleAccent)
                        .accessibilityLabel("Yarım kalan bölüm ilerlemesi")
                        .accessibilityValue("%\(draftProgress)")
                    }
                }

                PlayButton(
                    title: hasDraft ? "Devam Et" : "Başla",
                    systemImage: hasDraft ? "arrow.uturn.forward" : "play.fill",
                    action: onContinue
                )
            }
            .padding(20)
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
            .padding(.vertical, 16)
            .background(Color.surfaceDark, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
            .overlay(RoundedRectangle(cornerRadius: 20, style: .continuous).stroke(Color.borderDark, lineWidth: 0.5))
        }
        .buttonStyle(.plain)
    }
}

/// Full-width primary action inside a card: prominent glass tinted neon cyan.
private struct PlayButton: View {
    let title: String
    let systemImage: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Label(title, systemImage: systemImage)
                .font(.app(17, .bold))
                .foregroundStyle(Color.onNeon)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 6)
        }
        .buttonStyle(.appGlassProminent)
        .tint(.neonCyan)
    }
}
