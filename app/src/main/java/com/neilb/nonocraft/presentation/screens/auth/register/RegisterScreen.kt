package com.neilb.nonocraft.presentation.screens.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.neilb.nonocraft.common.buildGoogleCredentialRequest
import com.neilb.nonocraft.common.signInWithGoogle
import com.neilb.nonocraft.presentation.screens.auth.AuthViewModel
import com.neilb.nonocraft.presentation.screens.auth.components.AuthBackRow
import com.neilb.nonocraft.presentation.screens.auth.components.AuthDivider
import com.neilb.nonocraft.presentation.screens.auth.components.AuthPasswordField
import com.neilb.nonocraft.presentation.screens.auth.components.AuthTextField
import com.neilb.nonocraft.presentation.screens.auth.components.GoogleSignInButton
import com.neilb.nonocraft.presentation.screens.auth.components.InfoBanner
import com.neilb.nonocraft.presentation.screens.auth.components.PasswordRequirementRow
import com.neilb.nonocraft.presentation.screens.auth.components.PasswordStrengthBar
import com.neilb.nonocraft.presentation.screens.auth.components.PrimaryAuthButton
import com.neilb.nonocraft.presentation.screens.auth.components.noRippleClickable
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onRegisterSuccess()
        }
    }

    val credentialManager = remember { CredentialManager.create(context) }
    val request = remember { buildGoogleCredentialRequest() }

    val isEmailValid = email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordLongEnough = password.length >= 8
    val isPasswordHasUpper   = password.any { it.isUpperCase() }
    val isPasswordHasDigit   = password.any { it.isDigit() }
    val canSubmit = username.isNotEmpty()
            && email.isNotEmpty()
            && isEmailValid
            && isPasswordLongEnough
            && !state.isLoading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .navigationBarsPadding()
            .imePadding(),
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
                    text = "Hesap Oluştur",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                )
                Spacer(Modifier.height(32.dp))

                GoogleSignInButton(
                    label = "Google ile kayıt ol",
                    onClick = {
                        scope.launch {
                            signInWithGoogle(context, credentialManager, request)
                                .onSuccess { idToken -> authViewModel.googleLogin(idToken) }
                                .onFailure { e -> authViewModel.showError(e.localizedMessage) }
                        }
                    },
                )

                AuthDivider(label = "veya e-posta ile")

                if (state.error != null) {
                    InfoBanner(
                        message = "Kayıt başarısız",
                        subMessage = state.error!!,
                        isError = true,
                        modifier = Modifier.padding(bottom = 14.dp),
                    )
                }

                AuthTextField(
                    value = username,
                    onValueChange = { username = it.trim().lowercase() },
                    label = "Kullanıcı adı",
                    placeholder = "@kullaniciadi",
                    isError = state.error != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next,
                    ),
                    trailingIcon = if (username.isNotEmpty() && state.error == null) {
                        { Text("✓", color = NeonCyan, fontSize = 15.sp) }
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
                    PasswordStrengthBar(password = password, modifier = Modifier.padding(top = 8.dp))
                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Column {
                            Text(
                                text = "GEREKSİNİMLER",
                                color = TextMuted,
                                fontSize = 11.sp,
                                letterSpacing = 0.8.sp,
                                fontFamily = AppFont,
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            PasswordRequirementRow("En az 8 karakter",   met = isPasswordLongEnough)
                            PasswordRequirementRow("Büyük harf içeriyor", met = isPasswordHasUpper)
                            PasswordRequirementRow("Rakam içeriyor",      met = isPasswordHasDigit)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryAuthButton(
                    text = "Kayıt Ol",
                    enabled = canSubmit,
                    isLoading = state.isLoading,
                    onClick = {
                        viewModel.register(email, username, password)
                    },
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextSecondary)) { append("Hesabın var mı? ") }
                        withStyle(SpanStyle(color = NeonCyan, fontWeight = FontWeight.SemiBold)) { append("Giriş Yap") }
                    },
                    fontSize = 13.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.noRippleClickable(onNavigateToLogin),
                )
            }
        }
    }
}