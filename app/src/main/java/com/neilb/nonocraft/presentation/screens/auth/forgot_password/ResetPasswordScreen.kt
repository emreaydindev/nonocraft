package com.neilb.nonocraft.presentation.screens.auth.forgot_password

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.neilb.nonocraft.common.showToast
import com.neilb.nonocraft.presentation.screens.auth.components.*
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun ResetPasswordScreen(
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var code      by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirm   by remember { mutableStateOf("") }

    val isCodeValid  = code.length == 6
    val hasMinLength = password.length >= 8
    val hasUpper     = password.any { it.isUpperCase() }
    val hasDigit     = password.any { it.isDigit() }
    val passwordsMatch = password.isNotEmpty() && password == confirm
    val allRequirementsMet = hasMinLength && hasUpper && hasDigit

    val canSubmit = isCodeValid && allRequirementsMet && passwordsMatch && !state.isLoading

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            context.showToast("Şifren başarıyla güncellendi.", isError = false)
            onResetSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .navigationBarsPadding(),
    ) {
        GridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 24.dp),
        ) {
            AuthBackRow(onBack = onNavigateBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Yeni Şifre Belirle",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "E-postana gönderdiğimiz 6 haneli kodu gir ve yeni şifreni belirle.",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontFamily = AppFont,
                    lineHeight = 22.sp,
                )
                Spacer(Modifier.height(32.dp))

                AuthTextField(
                    value = code,
                    onValueChange = {
                        code = it.filter { ch -> ch.isDigit() }.take(6)
                    },
                    label = "Doğrulama kodu",
                    placeholder = "000000",
                    isError = state.error != null,
                    errorMessage = state.error,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = ImeAction.Next,
                    ),
                )

                Spacer(Modifier.height(20.dp))

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

                Spacer(Modifier.height(16.dp))

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
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryAuthButton(
                    text = "Şifremi Güncelle",
                    enabled = canSubmit,
                    isLoading = state.isLoading,
                    onClick = { viewModel.resetPassword(code, password) },
                )
            }
        }
    }
}