import Shared
import SwiftUI

struct RegisterScreen: View {
    let onNavigateToLogin: () -> Void
    let onGoogleSignIn: () -> Void
    let onRegisterSuccess: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.registerViewModel() }, state: \.state)

    @State private var username = ""
    @State private var email = ""
    @State private var password = ""
    @FocusState private var focus: Field?

    private enum Field { case username, email, password }

    private var uiState: RegisterUiState { model.state }
    private var isEmailValid: Bool { isValidEmailOrEmpty(email) }
    private var isPasswordLongEnough: Bool { password.count >= 8 }
    private var canSubmit: Bool {
        !username.isEmpty && !email.isEmpty && isEmailValid && isPasswordLongEnough && !uiState.isLoading
    }

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {

                CenteredScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Hesap Oluştur")
                            .font(.app(32, .bold))
                            .foregroundColor(.textPrimary)

                        GoogleSignInButton(label: "Google ile kayıt ol", action: onGoogleSignIn)
                            .padding(.top, 32)
                        AuthDivider(label: "veya e-posta ile")

                        if let error = uiState.error {
                            InfoBanner(message: "Kayıt başarısız", subMessage: error.message(.register), isError: true)
                                .padding(.bottom, 14)
                        }

                        AuthTextField(
                            text: $username, label: "Kullanıcı adı", placeholder: "@kullaniciadi",
                            isError: uiState.error != nil, keyboardType: .asciiCapable, contentType: .username
                        ) {
                            if !username.isEmpty && uiState.error == nil {
                                Text("✓").font(.system(size: 15)).foregroundColor(.neonCyan)
                            }
                        }
                        .focused($focus, equals: .username)
                        .onSubmit { focus = .email }
                        .onChange(of: username) { _, value in
                            username = value.trimmingCharacters(in: .whitespaces).lowercased()
                        }

                        AuthTextField(
                            text: $email, label: "E-posta", placeholder: "ornek@gmail.com",
                            isError: !isEmailValid, errorMessage: "Geçerli bir e-posta adresi girin",
                            keyboardType: .emailAddress, contentType: .emailAddress
                        )
                        .focused($focus, equals: .email)
                        .onSubmit { focus = .password }
                        .onChange(of: email) { _, value in
                            email = value.trimmingCharacters(in: .whitespaces)
                        }
                        .padding(.top, 12)

                        AuthPasswordField(text: $password, label: "Şifre", contentType: .newPassword)
                            .focused($focus, equals: .password)
                            .onSubmit(submit)
                            .padding(.top, 12)

                        if !password.isEmpty {
                            PasswordStrengthBar(password: password)
                                .padding(.top, 8)

                            VStack(alignment: .leading, spacing: 0) {
                                Text("GEREKSİNİMLER")
                                    .font(.app(11))
                                    .kerning(0.8)
                                    .foregroundColor(.textMuted)
                                    .padding(.bottom, 8)
                                PasswordRequirementRow(text: "En az 8 karakter", met: isPasswordLongEnough)
                                PasswordRequirementRow(text: "Büyük harf içeriyor", met: password.contains(where: \.isUppercase))
                                PasswordRequirementRow(text: "Rakam içeriyor", met: password.contains(where: \.isNumber))
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(Color.surfaceDark)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .padding(.top, 16)
                        }
                    }
                    .padding(.horizontal, 28)
                    .padding(.bottom, 24)
                }

                VStack(spacing: 0) {
                    PrimaryAuthButton(text: "Kayıt Ol", enabled: canSubmit, isLoading: uiState.isLoading, action: submit)
                    Button(action: onNavigateToLogin) {
                        (Text("Hesabın var mı? ").foregroundColor(.textSecondary)
                            + Text("Giriş Yap").font(.app(13, .semibold)).foregroundColor(.neonCyan))
                            .font(.app(13))
                    }
                    .buttonStyle(.plain)
                    .padding(.top, 16)
                    LegalText()
                        .padding(.horizontal, 8)
                        .padding(.top, 20)
                }
                .padding(.horizontal, 28)
                .padding(.bottom, 24)
            }
            .readableWidth()
        }
        // System back button on a transparent bar; the heading stays in the content.
        .toolbarBackground(.hidden, for: .navigationBar)
        .onChange(of: uiState.isSuccess) { _, isSuccess in
            if isSuccess { onRegisterSuccess() }
        }
    }

    private func submit() {
        guard canSubmit else { return }
        focus = nil
        model.viewModel.register(email: email, username: username, password: password)
    }
}
