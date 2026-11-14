import Shared
import SwiftUI

struct EditProfileScreen: View {
    let onNavigateBack: () -> Void

    @StateObject private var model = HeldViewModel { $0.editProfileViewModel() }
    @State private var username = ""
    @State private var email = ""
    @State private var bio = ""
    @State private var isLoading = false
    @State private var errorMessage: String?

    var body: some View {
        SettingsScaffold(title: "Profili Düzenle", onBack: onNavigateBack) {
            VStack(spacing: 20) {
                OutlinedField(label: "Kullanıcı Adı", text: $username)
                OutlinedField(label: "E-posta Adresi", text: $email, isEnabled: false)
                OutlinedField(label: "Hakkımda", text: $bio, isMultiline: true)

                if let errorMessage {
                    InfoBanner(message: "Profil güncellenemedi", subMessage: errorMessage, isError: true)
                }

                SettingsActionButton(title: "Değişiklikleri Kaydet", isLoading: isLoading, isEnabled: !username.isEmpty) {
                    errorMessage = nil
                    model.viewModel.updateProfile(username: username, bio: bio)
                }
                .padding(.top, 24)
            }
        }
        .task {
            // Prefill once from the current session.
            for await user in model.viewModel.currentUser {
                guard let user else { continue }
                username = user.username
                email = user.email
                bio = user.bio ?? ""
                break
            }
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .loading: isLoading = true
                case .success:
                    isLoading = false
                    onNavigateBack()
                case .error(let error):
                    isLoading = false
                    errorMessage = error.message
                }
            }
        }
    }
}
