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
            VStack(spacing: 0) {
                TabView(selection: $page) {
                    ForEach(slides.indices, id: \.self) { index in
                        FeatureSlideContent(slide: slides[index]).tag(index)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .always))
                .indexViewStyle(.page(backgroundDisplayMode: .interactive))

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
                        .padding(.horizontal, 8)
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 16)
                .frame(maxWidth: 560)
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

    private func overlay<Content: View>(opacity: Double, @ViewBuilder content: () -> Content) -> some View {
        ZStack {
            Color.backgroundDark.opacity(opacity).ignoresSafeArea()
            VStack(spacing: 16, content: content)
        }
        .transition(.opacity)
    }

    private struct FeatureSlideContent: View {
        let slide: FeatureSlide

        var body: some View {
            CenteredScrollView {
                VStack(spacing: 0) {
                    Image(systemName: slide.icon)
                        .font(.system(size: 32))
                        .foregroundColor(slide.color)
                        .frame(width: 72, height: 72)
                        .background(slide.color.opacity(0.15))
                        .clipShape(Circle())

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
                .padding(.bottom, 48)
            }
        }
    }

    private struct PlanSelectionCard: View {
        let plan: Plan
        let isSelected: Bool
        let action: () -> Void

        var body: some View {
            Button(action: action) {
                HStack {
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
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text(plan.price)
                            .font(.app(20, .bold))
                            .foregroundColor(.textPrimary)
                        Text(plan.period)
                            .font(.footnote)
                            .foregroundColor(.textMuted)
                    }
                }
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
