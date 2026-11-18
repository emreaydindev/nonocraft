import Shared
import SwiftUI

/// Paywall, presented as a sheet. The purchase is simulated by the shared PremiumViewModel
/// (no store integration yet), exactly as on Android.
struct PremiumScreen: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var model = HeldViewModel { $0.premiumViewModel() }
    @State private var isProcessing = false
    @State private var showSuccess = false
    @State private var selectedPlanId = "yearly"
    @State private var page = 0
    @Environment(\.dynamicTypeSize) private var dynamicTypeSize

    private struct Plan: Identifiable {
        let id: String
        let title: String
        let price: String
        let period: String
        var monthlyEquivalent: String?
        let accent: Color
    }

    private struct FeatureSlide {
        let title: String
        let description: String
        let icon: String
        let color: Color
        let bulletPoints: [String]
    }

    private let plans = [
        Plan(id: "monthly", title: "Aylık Plan", price: "₺89", period: "/ay", accent: .neonCyan),
        Plan(id: "yearly", title: "Yıllık Plan", price: "₺599", period: "/yıl", monthlyEquivalent: "Aylık ₺49.90'a gelir", accent: .gameGold),
    ]

    private let slides = [
        FeatureSlide(
            title: "Sınırsız Oyun Deneyimi",
            description: "Reklamları kaldırın ve bekleme sürelerine veda edin.",
            icon: "bolt.circle.fill", color: .neonCyan,
            bulletPoints: [
                "Hiçbir reklam gösterilmez.",
                "Sınırsız kristal ile can doldurma beklemesi yok.",
                "Hata payı 3 yerine 5 kalbe çıkarılır.",
                "Çevrimdışı oynamak için 50'ye kadar bölüm otomatik indirilir.",
            ]
        ),
        FeatureSlide(
            title: "Yaratıcılığınızı Özgür Bırakın",
            description: "Yapay zeka ile yeni bulmacalar ve tasarımlar üretin.",
            icon: "sparkles", color: .purpleAccent,
            bulletPoints: [
                "AI ile piksel sanat tasarımları oluşturun.",
                "Tek renkli çizimlerinizi AI ile saniyeler içinde renklendirin.",
                "Çizimleriniz çözülebilirlik algoritmasına takılınca AI'dan ipucu alın.",
            ]
        ),
        FeatureSlide(
            title: "Öne Çıkın ve Kişiselleştirin",
            description: "Toplulukta fark yaratın ve uygulamayı kendinize göre özelleştirin.",
            icon: "rosette", color: .gameGold,
            bulletPoints: [
                "Paylaşımlarınız Keşfet bölümünde algoritma tarafından daha çok öne çıkarılır.",
                "Profilinizde prestijli altın renkli highlight sergilenir.",
                "Uygulama ikonu ve teması için özel premium seçeneklere erişim.",
            ]
        ),
    ]

    private var selectedPlan: Plan { plans.first { $0.id == selectedPlanId } ?? plans[1] }

    var body: some View {
        NavigationStack {
            Group {
                if dynamicTypeSize.isAccessibilitySize {
                    // Large text: one scrolling page with every feature listed, then the plans.
                    ScrollView {
                        VStack(spacing: 32) {
                            ForEach(slides.indices, id: \.self) { index in
                                FeatureSlideContent(slide: slides[index], isEmbedded: true)
                            }
                            purchaseSection
                        }
                        .readableWidth()
                    }
                } else {
                    // Wide, short windows (the Duo inner screen, iPad landscape) put the plans beside the
                    // carousel, the same rule as the game screen; otherwise they sit below it.
                    GeometryReader { proxy in
                        let isSideBySide = proxy.size.width >= 700 && proxy.size.width > proxy.size.height * 1.15
                        let layout = isSideBySide
                            ? AnyLayout(HStackLayout(alignment: .center, spacing: 0))
                            : AnyLayout(VStackLayout(spacing: 0))
                        layout {
                            VStack(spacing: 8) {
                                TabView(selection: $page) {
                                    ForEach(slides.indices, id: \.self) { index in
                                        FeatureSlideContent(slide: slides[index], isEmbedded: false).tag(index)
                                    }
                                }
                                .tabViewStyle(.page(indexDisplayMode: .never))
                                // Drawn below the pages rather than over them, so a page that scrolls on a
                                // short screen (the Duo outer screen) never runs under the dots.
                                pageDots
                            }

                            if isSideBySide {
                                CenteredScrollView { purchaseSection }
                                    .frame(width: min(420, proxy.size.width * 0.42))
                            } else {
                                purchaseSection
                            }
                        }
                    }
                }
            }
            .background { ScreenBackground { EmptyView() } }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Kapat", systemImage: "xmark") { dismiss() }
                }
                ToolbarItem(placement: .principal) {
                    Label("Nonocraft Pro", systemImage: "crown.fill")
                        .labelStyle(.titleAndIcon)
                        .font(.app(17, .bold))
                        .foregroundStyle(Color.gameGold)
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .overlay {
                if isProcessing {
                    overlay(opacity: 0.85) {
                        ProgressView().tint(.gameGold).controlSize(.large)
                        Text("İşleminiz tamamlanıyor...")
                            .font(.headline)
                            .foregroundStyle(Color.textPrimary)
                    }
                }
                if showSuccess {
                    overlay(opacity: 0.95) {
                        Image(systemName: "crown.fill")
                            .font(.system(size: 36))
                            .foregroundStyle(Color.gameGold)
                            .frame(width: 80, height: 80)
                            .background(Color.gameGold.opacity(0.2), in: Circle())
                            .symbolEffect(.bounce, value: showSuccess)
                        Text("Aramıza Hoş Geldin!")
                            .font(.app(24, .bold))
                            .foregroundStyle(Color.gameGold)
                        Text("Tüm premium özellikler aktif edildi.")
                            .font(.subheadline)
                            .foregroundStyle(Color.textSecondary)
                    }
                }
            }
        }
        // Sheets don't inherit the dashboard tint on iOS 18, so the close button would turn system blue.
        .tint(.purpleAccent)
        .interactiveDismissDisabled(isProcessing)
        .task {
            for await processing in model.viewModel.isProcessing {
                isProcessing = processing.boolValue
            }
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .purchaseSuccess:
                    showSuccess = true
                    try? await Task.sleep(for: .milliseconds(2500))
                    dismiss()
                }
            }
        }
    }

    private var pageDots: some View {
        HStack(spacing: 8) {
            ForEach(slides.indices, id: \.self) { index in
                Circle()
                    .fill(index == page ? slides[index].color : Color.surfaceVariantDark)
                    .frame(width: 7, height: 7)
            }
        }
        .animation(.easeInOut(duration: 0.2), value: page)
        .padding(.bottom, 4)
        .accessibilityElement()
        .accessibilityLabel("Sayfa \(page + 1) / \(slides.count)")
    }

    private var purchaseSection: some View {
        VStack(spacing: 12) {
            ForEach(plans) { plan in
                PlanSelectionCard(plan: plan, isSelected: plan.id == selectedPlanId) {
                    selectedPlanId = plan.id
                }
            }

            Button {
                model.viewModel.purchasePremium(planId: selectedPlan.id)
            } label: {
                Text("Devam Et (\(selectedPlan.price)\(selectedPlan.period))")
                    .font(.app(17, .bold))
                    .foregroundStyle(Color.onNeon)
                    .multilineTextAlignment(.center)
                    .fixedSize(horizontal: false, vertical: true)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 6)
            }
            .buttonStyle(.appGlassProminent)
            .tint(selectedPlan.accent)
            .disabled(isProcessing)
            .animation(.easeInOut(duration: 0.2), value: selectedPlanId)
            .padding(.top, 8)

            Text("Abonelik seçilen plana göre otomatik yenilenir. İstediğin zaman iptal edebilirsin.")
                .font(.caption2)
                .foregroundStyle(Color.textSecondary)
                .multilineTextAlignment(.center)
                .fixedSize(horizontal: false, vertical: true)
                .padding(.horizontal, 8)
        }
        .padding(.horizontal, 24)
        .padding(.bottom, 16)
        .frame(maxWidth: 560)
    }

    private func overlay<Content: View>(opacity: Double, @ViewBuilder content: () -> Content) -> some View {
        ZStack {
            Color.backgroundDark.opacity(opacity).ignoresSafeArea()
            VStack(spacing: 16, content: content)
        }
        .transition(.opacity)
    }

    private struct FeatureSlideContent: View {
        let slide: FeatureSlide
        /// In the large-text page, which scrolls as a whole; otherwise a carousel page that scrolls itself.
        let isEmbedded: Bool

        var body: some View {
            if isEmbedded {
                content
            } else {
                CenteredScrollView { content }
            }
        }

        private var content: some View {
            VStack(spacing: 0) {
                Image(systemName: slide.icon)
                    .font(.system(size: 32))
                    .foregroundColor(slide.color)
                    .frame(width: 72, height: 72)
                    .background(slide.color.opacity(0.15))
                    .clipShape(Circle())
                    .accessibilityHidden(true)

                Text(slide.title)
                    .font(.app(24, .bold))
                    .foregroundColor(.textPrimary)
                    .multilineTextAlignment(.center)
                    .padding(.top, 24)

                Text(slide.description)
                    .font(.subheadline)
                    .foregroundColor(.textSecondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 16)
                    .padding(.top, 12)

                VStack(alignment: .leading, spacing: 16) {
                    ForEach(slide.bulletPoints, id: \.self) { point in
                        HStack(alignment: .top, spacing: 12) {
                            Image(systemName: "checkmark")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(slide.color)
                                .frame(width: 20, height: 20)
                                .accessibilityHidden(true) // the symbol reads as "Selected"
                            Text(point)
                                .font(.subheadline)
                                .foregroundColor(.textPrimary)
                        }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.top, 28)
            }
            .padding(.horizontal, 32)
            .padding(.top, 8)
            .padding(.bottom, isEmbedded ? 0 : 48)
        }
    }

    private struct PlanSelectionCard: View {
        let plan: Plan
        let isSelected: Bool
        let action: () -> Void

        @Environment(\.dynamicTypeSize) private var dynamicTypeSize

        var body: some View {
            Button(action: action) {
                AdaptiveStack(spacing: 8) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(plan.title)
                            .font(.app(16, .semibold))
                            .foregroundColor(.textPrimary)
                        if let equivalent = plan.monthlyEquivalent {
                            Text(equivalent)
                                .font(.footnote)
                                .foregroundColor(.textMuted)
                        }
                    }
                    Spacer(minLength: 0)
                    VStack(alignment: dynamicTypeSize.isAccessibilitySize ? .leading : .trailing, spacing: 0) {
                        Text(plan.price)
                            .font(.app(20, .bold))
                            .foregroundColor(.textPrimary)
                        Text(plan.period)
                            .font(.footnote)
                            .foregroundColor(.textMuted)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(16)
                .background(isSelected ? plan.accent.opacity(0.08) : Color.surfaceDark)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(
                    RoundedRectangle(cornerRadius: 16)
                        .stroke(isSelected ? plan.accent : Color.borderDark, lineWidth: isSelected ? 2 : 1)
                )
                .animation(.easeInOut(duration: 0.2), value: isSelected)
            }
            .buttonStyle(.plain)
            .accessibilityAddTraits(isSelected ? .isSelected : [])
        }
    }
}
