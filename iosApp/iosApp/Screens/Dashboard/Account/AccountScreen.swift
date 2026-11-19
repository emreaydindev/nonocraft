import Shared
import SwiftUI

struct AccountScreen: View {
    let onNavigateToPremium: () -> Void
    let onNavigateToSettings: () -> Void
    let onNavigateToCrafting: () -> Void
    let onNavigateToAuthGateway: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.accountViewModel() }, state: \.uiState)

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                DashboardCard(cornerRadius: 24) {
                    ProfileHeader(
                        uiState: model.state,
                        onNavigateToCrafting: onNavigateToCrafting,
                        onNavigateToAuthGateway: onNavigateToAuthGateway
                    )
                }
                PremiumBanner(action: onNavigateToPremium)
            }
            .padding(.horizontal, 18)
            .padding(.top, 8)
            .padding(.bottom, 24)
            .frame(maxWidth: 640)
            .frame(maxWidth: .infinity)
        }
        .background { ScreenBackground { EmptyView() } }
        .navigationTitle("Profilim")
        // Large title on the same row as the toolbar button instead of a row below it.
        .toolbarTitleDisplayMode(.inlineLarge)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Ayarlar", systemImage: "gearshape", action: onNavigateToSettings)
            }
        }
    }
}

/// Gold glass call to action for Pro (DECISIONS §3).
private struct PremiumBanner: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 14) {
                Image(systemName: "crown.fill")
                    .font(.title2)
                    .foregroundStyle(Color.gameGold)
                    .frame(width: 44, height: 44)
                    .background(Color.gameGold.opacity(0.18), in: Circle())
                VStack(alignment: .leading, spacing: 2) {
                    Text("Premium'a Geç")
                        .font(.app(17, .bold))
                        .foregroundStyle(Color.gameGold)
                    Text("Sınırsız kristal, ipucu ve özel temalar")
                        .font(.subheadline)
                        .foregroundStyle(Color.textPrimary.opacity(0.75))
                }
                Spacer(minLength: 8)
                Image(systemName: "chevron.right")
                    .font(.footnote.weight(.semibold))
                    .foregroundStyle(Color.textSecondary)
            }
            .padding(16)
            .contentShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
        }
        .buttonStyle(.plain)
        .appGlass(.regular.tint(.gameGold.opacity(0.12)).interactive(), in: RoundedRectangle(cornerRadius: 20, style: .continuous))
    }
}

private struct ProfileHeader: View {
    let uiState: AccountUiState
    let onNavigateToCrafting: () -> Void
    let onNavigateToAuthGateway: () -> Void

    /// The username is shown as "@name", so the initial is its second character.
    private var initial: String {
        if uiState.isGuest { return "?" }
        return uiState.username.dropFirst().first.map { String($0).uppercased() } ?? "N"
    }

    var body: some View {
        VStack(spacing: 0) {
            Button(action: onNavigateToCrafting) {
                Text(initial)
                    .font(.app(32, .bold))
                    .foregroundColor(.neonCyan)
                    .frame(width: 80, height: 80)
                    .background(Color.surfaceVariantDark)
                    .clipShape(Circle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Avatarı düzenle")

            if uiState.isGuest {
                Text("Misafir Kullanıcı")
                    .font(.app(20, .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.top, 12)
                Text("İlerlemeni kaybetmemek için giriş yap")
                    .font(.subheadline)
                    .foregroundColor(.textMuted)
                    .padding(.top, 4)
                PrimaryAuthButton(text: "Giriş Yap / Kayıt Ol", action: onNavigateToAuthGateway)
                    .padding(.top, 16)
            } else {
                Text(uiState.username)
                    .font(.app(20, .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.top, 12)
                Text(uiState.bio ?? "Nonocraft Bulmaca Meraklısı")
                    .font(.subheadline)
                    .foregroundColor(.textMuted)
                    .multilineTextAlignment(.center)
                    .padding(.top, 4)
                    .padding(.bottom, 20)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 24)
        .padding(.vertical, 28)
    }
}
