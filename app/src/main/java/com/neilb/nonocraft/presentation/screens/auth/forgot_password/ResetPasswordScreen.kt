package com.neilb.nonocraft.presentation.screens.auth.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.screens.auth.components.*
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun ResetPasswordScreen(
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit,
) {
    var password  by remember { mutableStateOf("") }
    var confirm   by remember { mutableStateOf("") }

    val hasMinLength = password.length >= 8
    val hasUpper     = password.any { it.isUpperCase() }
    val hasDigit     = password.any { it.isDigit() }
    val passwordsMatch = password.isNotEmpty() && password == confirm
    val allRequirementsMet = hasMinLength && hasUpper && hasDigit

    val canSubmit = allRequirementsMet && passwordsMatch

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
                    Text("🔑", fontSize = 24.sp)
                },
            )
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Yeni Şifre Belirle",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Güçlü bir şifre seç. Bir daha unutma!",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(28.dp))

            AuthPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Yeni şifre",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                ),
            )

            if (password.isNotEmpty()) {
                PasswordStrengthBar(
                    password = password,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            Spacer(Modifier.height(12.dp))

            AuthPasswordField(
                value = confirm,
                onValueChange = { confirm = it },
                label = "Şifreyi onayla",
                isError = confirm.isNotEmpty() && !passwordsMatch,
                errorMessage = if (confirm.isNotEmpty() && !passwordsMatch) "Şifreler eşleşmiyor" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            )

            if (passwordsMatch) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "✓ Şifreler eşleşiyor",
                    color = NeonCyan.copy(alpha = 0.80f),
                    fontSize = 11.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(SurfaceDark)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Column {
                    Text(
                        text = "GEREKSİNİMLER",
                        color = TextMuted,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp,
                        fontFamily = AppFont,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    PasswordRequirementRow("En az 8 karakter",   met = hasMinLength)
                    PasswordRequirementRow("Büyük harf içeriyor", met = hasUpper)
                    PasswordRequirementRow("Rakam içeriyor",      met = hasDigit)
                }
            }

            Spacer(Modifier.weight(1f))

            PrimaryAuthButton(
                text = "Şifremi Güncelle",
                enabled = canSubmit,
                onClick = {
                    // TODO: POST /auth/reset-password { token (deep link'ten), password }
                    //   başarılıysa → onResetSuccess()
                    onResetSuccess()
                },
            )
        }
    }
}