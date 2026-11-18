import Shared
import SwiftUI

struct SplashScreen: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    let onFinished: (SplashDestination) -> Void

    @StateObject private var store = ViewModelStore()
    @State private var logoVisible = false
    @State private var nameVisible = false
    @State private var tagVisible = false
    @State private var dotsVisible = false

    // Same timeline as the Android splash.
    private let logoStart = 0.6, nameStart = 1.5, tagStart = 1.9, dotsStart = 2.2, navigationDelay = 3.2

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {
                ZStack {
                    RadialGradient(
                        colors: [.neonCyan.opacity(0.18), .neonCyan.opacity(0.06), .clear],
                        center: .center, startRadius: 0, endRadius: 160
                    )
                    .frame(width: 320, height: 320)
                    .opacity(logoVisible ? 0.7 : 0)

                    Image("Logo")
                        .resizable()
                        .frame(width: 140, height: 140)
                        // Reduce Motion: fades in without the zoom.
                        .scaleEffect(logoVisible || reduceMotion ? 1 : 0.6)
                        .opacity(logoVisible ? 1 : 0)
                }
                .frame(height: 140)

                BrandName(size: 34, leadingColor: .white)
                    .kerning(1)
                    .padding(.top, 28)
                    .opacity(nameVisible ? 1 : 0)
                    .offset(y: nameVisible || reduceMotion ? 0 : 12)

                Text("Çöz ve Tasarla")
                    .font(.app(14))
                    .kerning(1)
                    .foregroundColor(.neonCyan.opacity(0.55))
                    .padding(.top, 10)
                    .opacity(tagVisible ? 1 : 0)
            }
        }
        .overlay(alignment: .bottom) {
            HStack(spacing: 8) {
                ForEach(0..<3, id: \.self) { index in
                    PulsingDot(delay: Double(index) * 0.2)
                }
            }
            .opacity(dotsVisible ? 1 : 0)
            .padding(.bottom, 60)
        }
        .task {
            withAnimation(.easeInOut(duration: 1.1).delay(logoStart)) { logoVisible = true }
            withAnimation(.easeInOut(duration: 0.7).delay(nameStart)) { nameVisible = true }
            withAnimation(.easeInOut(duration: 0.6).delay(tagStart)) { tagVisible = true }
            withAnimation(.easeInOut(duration: 0.4).delay(dotsStart)) { dotsVisible = true }

            async let minimumTime: Void = { try? await Task.sleep(for: .seconds(navigationDelay)) }()
            let destination = await resolvedDestination()
            await minimumTime
            if let destination, !Task.isCancelled {
                onFinished(destination)
            }
        }
    }

    /// First destination the ViewModel emits other than `Loading`.
    private func resolvedDestination() async -> SplashDestination? {
        for await destination in store.owner.splashViewModel().destination {
            if case .loading = onEnum(of: destination) { continue }
            return destination
        }
        return nil
    }
}

private struct PulsingDot: View {
    let delay: Double
    @State private var dimmed = true
    @Environment(\.accessibilityReduceMotion) private var reduceMotion

    var body: some View {
        RoundedRectangle(cornerRadius: 1.5)
            .fill(Color.neonCyan)
            .frame(width: 6, height: 6)
            // Reduce Motion: steady dots instead of a pulse.
            .opacity(reduceMotion ? 0.6 : dimmed ? 0.25 : 1)
            .onAppear {
                guard !reduceMotion else { return }
                withAnimation(.easeInOut(duration: 0.7).repeatForever(autoreverses: true).delay(delay)) {
                    dimmed = false
                }
            }
    }
}
