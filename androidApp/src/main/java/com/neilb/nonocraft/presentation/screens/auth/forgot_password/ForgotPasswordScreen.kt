package com.neilb.nonocraft.presentation.screens.auth.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.neilb.nonocraft.presentation.screens.auth.components.*
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    onCodeSentSuccess: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }

    val isEmailValid = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val canSubmit    = email.isNotEmpty() && isEmailValid && !state.isLoading

    LaunchedEffect(state.isSent) {
        if (state.isSent) {
            onCodeSentSuccess()
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
                    text = "Şifremi Unuttum",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "E-posta adresini gir, sana bir doğrulama kodu gönderelim.",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontFamily = AppFont,
                    lineHeight = 22.sp,
                )
                Spacer(Modifier.height(32.dp))

                AuthTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim()
                        viewModel.clearError()
                    },
                    label = "E-posta adresi",
                    placeholder = "ornek@gmail.com",
                    isError = !isEmailValid || state.error != null,
                    errorMessage = when {
                        !isEmailValid   -> "Geçerli bir e-posta adresi girin"
                        state.error != null -> state.error
                        else            -> null
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done,
                    ),
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryAuthButton(
                    text = "Kod Gönder",
                    enabled = canSubmit,
                    isLoading = state.isLoading,
                    onClick = { viewModel.sendResetCode(email) },
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextSecondary)) { append("Giriş sayfasına ") }
                        withStyle(SpanStyle(color = NeonCyan, fontWeight = FontWeight.SemiBold)) { append("dön") }
                    },
                    fontSize = 13.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.rippleClickable(onNavigateBack),
                )
            }
        }
    }
}