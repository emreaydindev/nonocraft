import Shared
import SwiftUI

struct ResetPasswordScreen: View {
    let onResetSuccess: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.resetPasswordViewModel() }, state: \.state)
    @State private var code = ""
    @State private var password = ""
    @State private var confirm = ""

    private var uiState: ResetPasswordUiState { model.state }
    private var hasMinLength: Bool { password.count >= 8 }
    private var hasUpper: Bool { password.contains(where: \.isUppercase) }
    private var hasDigit: Bool { password.contains(where: \.isNumber) }
    private var passwordsMatch: Bool { !password.isEmpty && password == confirm }
    private var canSubmit: Bool {
        code.count == 6 && hasMinLength && hasUpper && hasDigit && passwordsMatch && !uiState.isLoading
    }

    var body: some View {
        ScreenBackground {
            VStack(spacing: 0) {

                CenteredScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        Text("Yeni Şifre Belirle")
                            .font(.app(32, .bold))
                            .foregroundColor(.textPrimary)
                        Text("E-postana gönderdiğimiz 6 haneli kodu gir ve yeni şifreni belirle.")
                            .font(.app(15))
                            .lineSpacing(4)
                            .foregroundColor(.textSecondary)
                            .padding(.top, 8)

                        AuthTextField(
                            text: $code, label: "Doğrulama kodu", placeholder: "000000",
                            isError: uiState.error != nil, errorMessage: uiState.error,
                            keyboardType: .numberPad, contentType: .oneTimeCode
                        )
                        .onChange(of: code) { _, value in
                            code = String(value.filter(\.isNumber).prefix(6))
                        }
                        .padding(.top, 32)

                        AuthPasswordField(text: $password, label: "Yeni şifre", contentType: .newPassword, submitLabel: .next)
                            .padding(.top, 20)
                        if !password.isEmpty {
                            PasswordStrengthBar(password: password)
                                .padding(.top, 6)
                        }

                        AuthPasswordField(
                            text: $confirm, label: "Şifreyi onayla",
                            isError: !confirm.isEmpty && !passwordsMatch, errorMessage: "Şifreler eşleşmiyor",
                            contentType: .newPassword
                        )
                        .onSubmit(submit)
                        .padding(.top, 12)

                        if passwordsMatch {
                            Text("✓ Şifreler eşleşiyor")
                                .font(.app(11))
                                .foregroundColor(.neonCyan.opacity(0.80))
                                .padding(.leading, 4)
                                .padding(.top, 4)
                        }

                        VStack(alignment: .leading, spacing: 0) {
                            Text("GEREKSİNİMLER")
                                .font(.app(10))
                                .kerning(0.8)
                                .foregroundColor(.textMuted)
                                .padding(.bottom, 8)
                            PasswordRequirementRow(text: "En az 8 karakter", met: hasMinLength)
                            PasswordRequirementRow(text: "Büyük harf içeriyor", met: hasUpper)
                            PasswordRequirementRow(text: "Rakam içeriyor", met: hasDigit)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 12)
                        .background(Color.surfaceDark)
                        .clipShape(RoundedRectangle(cornerRadius: 13))
                        .padding(.top, 16)
                        .padding(.bottom, 24)
                    }
                    .padding(.horizontal, 28)
                }

                PrimaryAuthButton(text: "Şifremi Güncelle", enabled: canSubmit, isLoading: uiState.isLoading, action: submit)
                    .padding(.horizontal, 28)
                    .padding(.bottom, 24)
            }
            .readableWidth()
        }
        // System back button on a transparent bar; the heading stays in the content.
        .toolbarBackground(.hidden, for: .navigationBar)
        .onChange(of: uiState.isSuccess) { _, isSuccess in
            if isSuccess { onResetSuccess() }
        }
    }

    private func submit() {
        guard canSubmit else { return }
        model.viewModel.resetPassword(code: code, password: password)
    }
}
