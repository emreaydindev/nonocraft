import Shared
import SwiftUI

/// Paywall. The purchase is simulated by the shared PremiumViewModel (no store integration yet),
/// exactly as on Android.
struct PremiumScreen: View {
    let onNavigateBack: () -> Void

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
        ZStack {
            Color.backgroundDark.ignoresSafeArea()

            VStack(spacing: 0) {
                AuthBackRow(onBack: onNavigateBack)

                TabView(selection: $page) {
                    ForEach(slides.indices, id: \.self) { index in
                        FeatureSlideContent(slide: slides[index]).tag(index)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))

                HStack(spacing: 8) {
                    ForEach(slides.indices, id: \.self) { index in
                        Capsule()
                            .fill(index == page ? slides[index].color : Color.borderDark)
                            .frame(width: index == page ? 24 : 8, height: 8)
                    }
                }
                .animation(.easeInOut(duration: 0.25), value: page)
                .padding(.vertical, 16)
                .accessibilityHidden(true)

                VStack(spacing: 12) {
                    ForEach(plans) { plan in
                        PlanSelectionCard(plan: plan, isSelected: plan.id == selectedPlanId) {
                            selectedPlanId = plan.id
                        }
                    }
                }
                .padding(.horizontal, 24)
                .padding(.vertical, 8)

                Button {
                    model.viewModel.purchasePremium(planId: selectedPlan.id)
                } label: {
                    Group {
                        if isProcessing {
                            ProgressView().tint(.backgroundDark)
                        } else {
                            Text("Devam Et (\(selectedPlan.price)\(selectedPlan.period))")
                                .font(.app(16, .bold))
                                .foregroundColor(.backgroundDark)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(selectedPlan.accent)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                }
                .buttonStyle(.plain)
                .disabled(isProcessing)
                .animation(.easeInOut(duration: 0.2), value: selectedPlanId)
                .padding(.horizontal, 24)
                .padding(.vertical, 16)

                Text("Abonelik seçilen plana göre otomatik yenilenir. İstediğin zaman iptal edebilirsin.")
                    .font(.app(10))
                    .foregroundColor(.textMuted)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 32)
                    .padding(.bottom, 24)
            }

            if isProcessing {
                overlay(opacity: 0.85) {
                    ProgressView().tint(.gameGold).controlSize(.large)
                    Text("İşleminiz tamamlanıyor...")
                        .font(.app(16, .medium))
                        .foregroundColor(.textPrimary)
                }
            }
            if showSuccess {
                overlay(opacity: 0.95) {
                    Image(systemName: "rosette")
                        .font(.system(size: 36))
                        .foregroundColor(.gameGold)
                        .frame(width: 80, height: 80)
                        .background(Color.gameGold.opacity(0.2))
                        .clipShape(Circle())
                    Text("Aramıza Hoş Geldin!")
                        .font(.app(24, .bold))
                        .foregroundColor(.gameGold)
                    Text("Tüm premium özellikler aktif edildi.")
                        .font(.app(14))
                        .foregroundColor(.textSecondary)
                }
            }
        }
        .toolbar(.hidden, for: .navigationBar)
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
                    onNavigateBack()
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
                        .font(.app(14))
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
                                    .font(.app(14))
                                    .lineSpacing(3)
                                    .foregroundColor(.textPrimary)
                            }
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top, 32)
                }
                .padding(.horizontal, 32)
                .padding(.vertical, 16)
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
                                .font(.app(12))
                                .foregroundColor(.textMuted)
                        }
                    }
                    Spacer()
                    VStack(alignment: .trailing, spacing: 0) {
                        Text(plan.price)
                            .font(.app(20, .bold))
                            .foregroundColor(.textPrimary)
                        Text(plan.period)
                            .font(.app(12))
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
