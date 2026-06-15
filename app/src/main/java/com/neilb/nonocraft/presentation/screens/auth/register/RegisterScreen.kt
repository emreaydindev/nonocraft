package com.neilb.nonocraft.presentation.screens.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isUsernameTaken   = username == "neilb"   // TODO: backend'e kullanıcı adı kontrol isteği at
    val isEmailValid      = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordLongEnough = password.length >= 8
    val isPasswordHasUpper   = password.any { it.isUpperCase() }
    val isPasswordHasDigit   = password.any { it.isDigit() }
    val canSubmit = username.isNotEmpty()
            && !isUsernameTaken
            && email.isNotEmpty()
            && isEmailValid
            && isPasswordLongEnough

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 16.dp, bottom = 36.dp),
        ) {

            AuthBackRow(onBack = onNavigateBack)
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Hesap Oluştur",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Birkaç saniyede hazır olursun.",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(24.dp))

            GoogleSignInButton(
                label = "Google ile kayıt ol",
                onClick = {
                    // TODO: Google OAuth akışını başlat
                },
            )
            AuthDivider(label = "veya e-posta ile")

            AuthTextField(
                value = username,
                onValueChange = { username = it.trim().lowercase() },
                label = "Kullanıcı adı",
                placeholder = "@kullaniciadi",
                isError = isUsernameTaken,
                errorMessage = if (isUsernameTaken) "Bu kullanıcı adı alınmış" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Next,
                ),
                // Kullanıcı adı müsaitse küçük yeşil tik göster
                trailingIcon = if (username.isNotEmpty() && !isUsernameTaken) {
                    { Text("✓", color = NeonCyan, fontSize = 14.sp) }
                } else null,
            )
            Spacer(Modifier.height(12.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it.trim() },
                label = "E-posta",
                placeholder = "ornek@gmail.com",
                isError = !isEmailValid,
                errorMessage = if (!isEmailValid) "Geçerli bir e-posta adresi girin" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(Modifier.height(12.dp))

            AuthPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Şifre",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            )

            if (password.isNotEmpty()) {
                PasswordStrengthBar(password = password, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Column {
                        Text(
                            text = "GEREKSİNİMLER",
                            color = TextMuted,
                            fontSize = 10.sp,
                            letterSpacing = 0.8.sp,
                            fontFamily = AppFont,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                        PasswordRequirementRow("En az 8 karakter",   met = isPasswordLongEnough)
                        PasswordRequirementRow("Büyük harf içeriyor", met = isPasswordHasUpper)
                        PasswordRequirementRow("Rakam içeriyor",      met = isPasswordHasDigit)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            PrimaryAuthButton(
                text = "Kayıt Ol",
                enabled = canSubmit,
                onClick = {
                    // TODO: POST /auth/register { username, email, password }
                    onRegisterSuccess()
                },
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextMuted)) { append("Hesabın var mı? ") }
                        withStyle(SpanStyle(color = NeonCyan))  { append("Giriş Yap") }
                    },
                    fontSize = 12.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.noRippleClickable(onNavigateToLogin),
                )
            }
        }
    }
}