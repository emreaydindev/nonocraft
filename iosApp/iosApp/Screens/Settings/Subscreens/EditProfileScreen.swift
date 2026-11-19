import Shared
import SwiftUI

struct EditProfileScreen: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var model = HeldViewModel { $0.editProfileViewModel() }
    @State private var username = ""
    @State private var email = ""
    @State private var bio = ""
    @State private var isLoading = false
    @State private var errorMessage: String?

    var body: some View {
        SettingsForm(title: "Profili Düzenle") {
            SettingsSection("Kullanıcı Adı") {
                TextField("Kullanıcı Adı", text: $username)
                    .textContentType(.username)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
            }
            SettingsSection(title: "E-posta Adresi") {
                Text(email.isEmpty ? " " : email)
                    .foregroundStyle(Color.textSecondary)
            } footer: {
                Text("E-posta adresi buradan değiştirilemez.")
            }
            SettingsSection(title: "Hakkımda") {
                TextField("Kendinden bahset", text: $bio, axis: .vertical)
                    .lineLimit(3...6)
            } footer: {
                if let errorMessage {
                    Label(errorMessage, systemImage: "exclamationmark.triangle.fill")
                        .foregroundStyle(Color.dangerRed)
                }
            }
        }
        .disabled(isLoading)
        .toolbar {
            ToolbarSaveButton(title: "Kaydet", isLoading: isLoading, isEnabled: !username.isEmpty) {
                errorMessage = nil
                model.viewModel.updateProfile(username: username, bio: bio)
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
                    dismiss()
                case .error(let error):
                    isLoading = false
                    errorMessage = error.code.message(.editProfile)
                }
            }
        }
    }
}
