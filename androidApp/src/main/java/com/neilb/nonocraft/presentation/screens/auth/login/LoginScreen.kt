package com.neilb.nonocraft.presentation.screens.auth.login

import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
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
import org.koin.androidx.compose.koinViewModel
import com.neilb.nonocraft.common.buildGoogleCredentialRequest
import com.neilb.nonocraft.common.showToast
import com.neilb.nonocraft.common.signInWithGoogle
import com.neilb.nonocraft.presentation.screens.auth.AuthViewModel
import com.neilb.nonocraft.presentation.screens.auth.components.AuthBackRow
import com.neilb.nonocraft.presentation.screens.auth.components.AuthDivider
import com.neilb.nonocraft.presentation.screens.auth.components.AuthPasswordField
import com.neilb.nonocraft.presentation.screens.auth.components.AuthTextField
import com.neilb.nonocraft.presentation.screens.auth.components.GoogleSignInButton
import com.neilb.nonocraft.presentation.screens.auth.components.InfoBanner
import com.neilb.nonocraft.presentation.screens.auth.components.PrimaryAuthButton
import com.neilb.nonocraft.presentation.screens.auth.components.rippleClickable
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var identifier by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            onLoginSuccess()
        }
    }

    val credentialManager = remember { CredentialManager.create(context) }
    val request = remember { buildGoogleCredentialRequest() }

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
                    text = "Tekrar Hoş Geldin",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Devam etmek için giriş yap.",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontFamily = AppFont,
                )
                Spacer(Modifier.height(32.dp))

                GoogleSignInButton(
                    label = "Google ile giriş yap",
                    onClick = {
                        scope.launch {
                            authViewModel.changeLoadingState(true)
                            signInWithGoogle(context, credentialManager, request)
                                .onSuccess { idToken -> authViewModel.googleLogin(idToken) }
                                .onFailure { e ->
                                    context.showToast(e.localizedMessage)
                                    authViewModel.changeLoadingState(false)
                                }
                        }
                    },
                    isLoading = authState.isLoading
                )

                AuthDivider(label = "veya e-posta ile")

                if (state.error != null) {
                    InfoBanner(
                        message = "Giriş başarısız",
                        subMessage = state.error!!,
                        isError = true,
                        modifier = Modifier.padding(bottom = 14.dp),
                    )
                }

                AuthTextField(
                    value = identifier,
                    onValueChange = {
                        identifier = it.trim()
                    },
                    label = "E-posta veya kullanıcı adı",
                    placeholder = "ornek@gmail.com veya @kullanici",
                    isError = state.error != null,
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
                    },
                    label = "Şifre",
                    isError = state.error != null,
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
                        fontSize = 13.sp,
                        fontFamily = AppFont,
                        modifier = Modifier.rippleClickable(onNavigateToForgotPassword),
                    )
                }

                Spacer(Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryAuthButton(
                    text = "Giriş Yap",
                    enabled = identifier.isNotEmpty() && password.isNotEmpty() && !state.isLoading,
                    isLoading = state.isLoading,
                    onClick = {
                        viewModel.login(identifier, password)
                    },
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextSecondary)) { append("Hesabın yok mu? ") }
                        withStyle(SpanStyle(color = NeonCyan, fontWeight = FontWeight.SemiBold)) { append("Kayıt Ol") }
                    },
                    fontSize = 13.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.rippleClickable(onNavigateToRegister),
                )
            }
        }
    }
}