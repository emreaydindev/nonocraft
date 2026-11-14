import Shared
import SwiftUI

struct AccountScreen: View {
    let onNavigateToPremium: () -> Void
    let onNavigateToSettings: () -> Void
    let onNavigateToCrafting: () -> Void
    let onNavigateToAuthGateway: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.accountViewModel() }, state: \.uiState)

    var body: some View {
        ScreenBackground {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    Text("Profilim")
                        .font(.app(28, .bold))
                        .foregroundColor(.textPrimary)
                        .padding(.vertical, 16)

                    DashboardCard(cornerRadius: 24) {
                        ProfileHeader(
                            uiState: model.state,
                            onNavigateToCrafting: onNavigateToCrafting,
                            onNavigateToAuthGateway: onNavigateToAuthGateway
                        )
                    }

                    MenuRow(icon: "rosette", iconColor: .gameGold, label: "Premium'a Geç", action: onNavigateToPremium)
                        .padding(.top, 24)
                    MenuRow(icon: "gearshape.fill", iconColor: .textSecondary, label: "Ayarlar", action: onNavigateToSettings)
                        .padding(.top, 8)
                }
                .padding(.horizontal, 18)
                .padding(.top, 16)
                .padding(.bottom, 24)
            }
        }
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
                    .font(.app(13))
                    .foregroundColor(.textMuted)
                    .padding(.top, 4)
                PrimaryAuthButton(text: "Giriş Yap / Kayıt Ol", action: onNavigateToAuthGateway)
                    .padding(.top, 16)
            } else {
                Text(uiState.username)
                    .font(.app(20, .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.top, 12)
                Text(uiState.bio)
                    .font(.app(14))
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

private struct MenuRow: View {
    let icon: String
    let iconColor: Color
    let label: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            DashboardCard(cornerRadius: 14) {
                HStack(spacing: 12) {
                    Image(systemName: icon)
                        .font(.system(size: 17))
                        .foregroundColor(iconColor)
                        .frame(width: 20, height: 20)
                    Text(label)
                        .font(.app(14, .medium))
                        .foregroundColor(.textPrimary)
                    Spacer()
                    Text("›")
                        .font(.system(size: 20))
                        .foregroundColor(.textMuted)
                }
                .padding(16)
            }
        }
        .buttonStyle(.plain)
    }
}
