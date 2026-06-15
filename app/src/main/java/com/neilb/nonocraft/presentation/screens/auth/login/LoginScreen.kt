package com.neilb.nonocraft.presentation.screens.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.screens.auth.components.*
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    var identifier by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    val canSubmit = identifier.isNotEmpty() && password.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
    ) {
        AuthGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 28.dp)
                .padding(top = 16.dp, bottom = 36.dp),
        ) {

            AuthBackRow(onBack = onNavigateBack)
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Tekrar Hoş Geldin",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Devam etmek için giriş yap.",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(24.dp))

            GoogleSignInButton(
                label = "Google ile giriş yap",
                onClick = {
                    // TODO: Google OAuth akışını başlat
                },
            )
            AuthDivider(label = "veya e-posta ile")

            if (loginError) {
                InfoBanner(
                    message = "E-posta veya şifre hatalı",
                    subMessage = "Bilgilerini kontrol edip tekrar dene.",
                    isError = true,
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }

            AuthTextField(
                value = identifier,
                onValueChange = {
                    identifier = it.trim()
                    loginError = false
                },
                label = "E-posta veya kullanıcı adı",
                placeholder = "ornek@gmail.com veya @kullanici",
                isError = loginError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(Modifier.height(12.dp))

            AuthPasswordField(
                value = password,
                onValueChange = {
                    password = it
                    loginError = false
                },
                label = "Şifre",
                isError = loginError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "Şifremi unuttum",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.noRippleClickable(onNavigateToForgotPassword),
                )
            }

            Spacer(Modifier.weight(1f))

            PrimaryAuthButton(
                text = "Giriş Yap",
                enabled = canSubmit,
                onClick = {
                    // TODO: POST /auth/login { identifier, password }
                    //   başarılıysa → onLoginSuccess()
                    //   401/403     → loginError = true
                    onLoginSuccess()
                },
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextMuted)) { append("Hesabın yok mu? ") }
                        withStyle(SpanStyle(color = NeonCyan))  { append("Kayıt Ol") }
                    },
                    fontSize = 12.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.noRippleClickable(onNavigateToRegister),
                )
            }
        }
    }
}