import SwiftUI

struct OnboardingScreen: View {
    let onFinishOnboarding: () -> Void

    private let craftColors: [Color] = [.neonCyan, .dangerRed, .gameGold, .purpleAccent, .brightGreen]
    @State private var selectedCraftColor = 3
    @State private var page = 0

    private struct Slide {
        let tag: String
        let title: String
        let titleAccent: String
        let description: String
        let accentColor: Color
    }

    private var slides: [Slide] {
        [
            Slide(
                tag: "BULMACA · MANTIK · SINIRSIZ", title: "Çöz!", titleAccent: "Çöz",
                description: "Her gün yenilenen, binlerce benzersiz nonogram bulmacasını keşfet. Mantığını konuştur.",
                accentColor: .neonCyan
            ),
            Slide(
                tag: "TASARIM · PAYLAŞIM · TOPLULUK", title: "Tasarla ve Paylaş!", titleAccent: "Paylaş",
                description: "Kendi pixel artını oluştur, toplulukla paylaş. Beğenileri topla, efsane ol.",
                accentColor: craftColors[selectedCraftColor]
            ),
            Slide(
                tag: "PREMIUM · AVANTAJLAR", title: "Sınırlarını Aş!", titleAccent: "Sınırlarını",
                description: "Premium ile reklamsız bir deneyim yaşa; sınırsız kristal, fazladan kalpler ve özel öne çıkarmalar kazan.",
                accentColor: .gameGold
            ),
        ]
    }

    var body: some View {
        let slides = slides
        let isLastPage = page == slides.count - 1
        let accent = slides[page].accentColor

        ScreenBackground {
            VStack(spacing: 0) {
                TabView(selection: $page) {
                    ForEach(slides.indices, id: \.self) { index in
                        slideContent(slides[index], index: index).tag(index)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))

                VStack(spacing: 16) {
                    HStack(spacing: 6) {
                        ForEach(slides.indices, id: \.self) { index in
                            Capsule()
                                .fill(index == page ? accent : Color.surfaceVariantDark)
                                .frame(width: index == page ? 24 : 8, height: 8)
                        }
                    }
                    .animation(.easeInOut(duration: 0.3), value: page)
                    .accessibilityHidden(true)

                    Button {
                        if isLastPage {
                            onFinishOnboarding()
                        } else {
                            withAnimation { page += 1 }
                        }
                    } label: {
                        Text(isLastPage ? "Başla" : "Devam Et")
                            .font(.app(15, .semibold))
                            .foregroundColor(.backgroundDark)
                            .frame(maxWidth: .infinity)
                            .frame(minHeight: 52)
                            .background(accent)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .buttonStyle(.plain)

                    Button("Atla", action: onFinishOnboarding)
                        .font(.app(13))
                        .foregroundColor(.textMuted)
                        .opacity(isLastPage ? 0 : 1)
                        .disabled(isLastPage)
                }
                .padding(.horizontal, 28)
                .padding(.top, 24)
                .padding(.bottom, 24)
            }
            .readableWidth()
        }
    }

    /// A fixed slide; scrolls at accessibility text sizes.
    private func slideContent(_ slide: Slide, index: Int) -> some View {
        LargeTextScrollView {
            VStack(spacing: 0) {
                Text(slide.tag)
                    .font(.app(11, .semibold))
                    .kerning(2)
                    .foregroundColor(slide.accentColor.opacity(0.7))
                    .padding(.bottom, 24)

                Group {
                    switch index {
                    case 0: VisualSolve(accent: slide.accentColor)
                    case 1: VisualCraft(colors: craftColors, selected: $selectedCraftColor)
                    default: VisualPremium(accent: slide.accentColor)
                    }
                }
                .frame(maxWidth: .infinity, minHeight: 160, maxHeight: .infinity)
                .aspectRatio(1.1, contentMode: .fit)
                .background(Color.surfaceDark)
                .clipShape(RoundedRectangle(cornerRadius: 24))
                .overlay(RoundedRectangle(cornerRadius: 24).stroke(slide.accentColor.opacity(0.30), lineWidth: 1))

                title(slide)
                    .font(.app(26, .bold))
                    .multilineTextAlignment(.center)
                    .fixedSize(horizontal: false, vertical: true)
                    .padding(.top, 32)
                    .padding(.bottom, 12)

                Text(slide.description)
                    .font(.app(14))
                    .lineSpacing(5)
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .padding(.horizontal, 28)
            // The Duo inner screen keeps its status bar at the side, so the top safe area is almost zero.
            .padding(.top, 16)
            .frame(maxHeight: .infinity)
        }
    }

    private func title(_ slide: Slide) -> Text {
        guard let range = slide.title.range(of: slide.titleAccent) else {
            return Text(slide.title).foregroundColor(.textPrimary)
        }
        return Text(slide.title[..<range.lowerBound]).foregroundColor(.textPrimary)
            + Text(slide.titleAccent).foregroundColor(slide.accentColor)
            + Text(slide.title[range.upperBound...]).foregroundColor(.textPrimary)
    }
}

/// A heart that fills in cell by cell, then restarts.
private struct VisualSolve: View {
    let accent: Color

    private let pattern = [
        [0, 1, 0, 0, 0, 1, 0],
        [1, 1, 1, 0, 1, 1, 1],
        [1, 1, 1, 1, 1, 1, 1],
        [0, 1, 1, 1, 1, 1, 0],
        [0, 0, 1, 1, 1, 0, 0],
        [0, 0, 0, 1, 0, 0, 0],
    ]
    private let columnClues = ["2", "4", "4", "4", "4", "4", "2"]
    @State private var filledCount = 0

    /// Order in which each filled cell lights up (row by row).
    private func fillOrder(row: Int, col: Int) -> Int {
        pattern[..<row].reduce(0) { $0 + $1.filter { $0 == 1 }.count } + pattern[row][...col].filter { $0 == 1 }.count
    }

    var body: some View {
        GeometryReader { proxy in
            // 7 cells plus the clue column, scaled to the card.
            let cell = min((proxy.size.width - 32) / 8.6, (proxy.size.height - 32) / 7.8)
            VStack(spacing: cell * 0.125) {
                HStack(spacing: cell * 0.125) {
                    Color.clear.frame(width: cell * 0.875, height: cell * 0.5)
                    ForEach(columnClues.indices, id: \.self) { index in
                        Text(columnClues[index])
                            .font(.appFixed(9, .medium))
                            .foregroundColor(accent)
                            .frame(width: cell)
                    }
                }
                ForEach(pattern.indices, id: \.self) { row in
                    HStack(spacing: cell * 0.125) {
                        Text("\(pattern[row].filter { $0 == 1 }.count)")
                            .font(.appFixed(9, .medium))
                            .foregroundColor(accent)
                            .frame(width: cell * 0.875, alignment: .trailing)
                        ForEach(pattern[row].indices, id: \.self) { col in
                            let isFilled = pattern[row][col] == 1 && fillOrder(row: row, col: col) <= filledCount
                            RoundedRectangle(cornerRadius: 4)
                                .fill(isFilled ? accent : Color.surfaceVariantDark)
                                .overlay(RoundedRectangle(cornerRadius: 4).stroke(Color.borderDark.opacity(isFilled ? 0 : 1), lineWidth: 0.5))
                                .frame(width: cell, height: cell)
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .task {
            let total = pattern.joined().filter { $0 == 1 }.count
            while !Task.isCancelled {
                for count in 0...total {
                    filledCount = count
                    try? await Task.sleep(for: .milliseconds(80))
                }
                try? await Task.sleep(for: .milliseconds(1200))
                filledCount = 0
                try? await Task.sleep(for: .milliseconds(400))
            }
        }
        .accessibilityHidden(true)
    }
}

/// Pixel-art sample whose colour follows the swatch the user picks.
private struct VisualCraft: View {
    let colors: [Color]
    @Binding var selected: Int

    private let pixels = [
        [0, 0, 1, 1, 1, 0, 0],
        [0, 1, 1, 0, 1, 1, 0],
        [1, 1, 1, 1, 1, 1, 1],
        [1, 0, 1, 1, 1, 0, 1],
        [0, 1, 1, 1, 1, 1, 0],
        [0, 0, 1, 0, 1, 0, 0],
        [0, 1, 0, 0, 0, 1, 0],
    ]

    var body: some View {
        GeometryReader { proxy in
            let cell = min((proxy.size.width - 32) / 7.8, (proxy.size.height - 96) / 7.8)
            VStack(spacing: 24) {
                VStack(spacing: 3) {
                    ForEach(pixels.indices, id: \.self) { row in
                        HStack(spacing: 3) {
                            ForEach(pixels[row].indices, id: \.self) { col in
                                let isOn = pixels[row][col] == 1
                                RoundedRectangle(cornerRadius: 3)
                                    .fill(isOn ? colors[selected] : Color.surfaceVariantDark)
                                    .overlay(RoundedRectangle(cornerRadius: 3).stroke(Color.borderDark.opacity(isOn ? 0 : 1), lineWidth: 0.5))
                                    .frame(width: cell, height: cell)
                            }
                        }
                    }
                }
                .accessibilityHidden(true)

                HStack(spacing: 8) {
                    ForEach(colors.indices, id: \.self) { index in
                        let isSelected = index == selected
                        Button {
                            selected = index
                        } label: {
                            Circle()
                                .fill(colors[index])
                                .frame(width: isSelected ? 28 : 22, height: isSelected ? 28 : 22)
                                .padding(3)
                                .overlay(Circle().stroke(colors[index].opacity(isSelected ? 1 : 0), lineWidth: 1.5))
                                .frame(width: 36, height: 36)
                        }
                        .buttonStyle(.plain)
                        .accessibilityLabel("Renk \(index + 1)")
                        .accessibilityAddTraits(isSelected ? .isSelected : [])
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

private struct VisualPremium: View {
    let accent: Color

    private let features = [
        "Reklamsız deneyim",
        "Sınırsız kristal paketleri",
        "Ekstra yaşam kalpleri",
        "Gönderilerini öne çıkar",
    ]

    var body: some View {
        VStack(spacing: 6) {
            Text("★")
                .font(.system(size: 22))
                .foregroundColor(accent)
                .frame(width: 56, height: 56)
                .background(accent.opacity(0.15))
                .clipShape(Circle())
                .overlay(Circle().stroke(accent.opacity(0.40), lineWidth: 1.5))
                .padding(.bottom, 6)

            ForEach(features, id: \.self) { feature in
                HStack(spacing: 0) {
                    Text("✓ ").font(.system(size: 14, weight: .bold))
                    Text(feature).font(.app(14))
                    Spacer(minLength: 0)
                }
                .foregroundColor(accent)
                .padding(.horizontal, 14)
                .padding(.vertical, 9)
                .background(accent.opacity(0.15))
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
}
