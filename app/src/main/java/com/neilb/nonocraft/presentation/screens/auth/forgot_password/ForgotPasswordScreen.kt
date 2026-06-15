package com.neilb.nonocraft.presentation.screens.auth.forgot_password

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
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onCodeSentSuccess: () -> Unit,
) {
    var email   by remember { mutableStateOf("") }
    var isSent  by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val isEmailValid = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val canSubmit    = email.isNotEmpty() && isEmailValid

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
            Spacer(Modifier.height(24.dp))

            AuthIconBox(
                icon = {
                    Text("🔒", fontSize = 24.sp)
                },
            )
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Şifremi Unuttum",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "E-posta adresini gir, sana sıfırlama bağlantısı gönderelim.",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = AppFont,
                lineHeight = 18.sp,
            )
            Spacer(Modifier.height(28.dp))

            AuthTextField(
                value = email,
                onValueChange = {
                    email = it.trim()
                    isSent = false
                    isError = false
                },
                label = "E-posta adresi",
                placeholder = "ornek@gmail.com",
                isError = !isEmailValid || isError,
                errorMessage = when {
                    !isEmailValid -> "Geçerli bir e-posta adresi girin"
                    isError       -> "Bu e-posta kayıtlı değil"
                    else          -> null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                ),
            )

            if (isSent) {
                Spacer(Modifier.height(12.dp))
                InfoBanner(
                    message = "Bağlantı gönderildi!",
                    subMessage = "Gelen kutunu kontrol et. Spam klasörünü de unutma.",
                )
            }

            Spacer(Modifier.weight(1f))

            PrimaryAuthButton(
                text = if (isSent) "Tekrar Gönder" else "Bağlantı Gönder",
                enabled = canSubmit,
                onClick = {
                    // TODO: POST /auth/forgot-password { email }
                    //   başarılıysa → isSent = true
                    //   404 (kayıtlı değil) → isError = true
                    isSent = true
                },
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextMuted)) { append("Giriş sayfasına ") }
                        withStyle(SpanStyle(color = NeonCyan))  { append("dön") }
                    },
                    fontSize = 12.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.noRippleClickable(onNavigateBack),
                )
            }
        }
    }
}