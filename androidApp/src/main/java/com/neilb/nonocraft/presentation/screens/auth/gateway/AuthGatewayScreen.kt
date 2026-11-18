package com.neilb.nonocraft.presentation.screens.auth.gateway

import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import org.koin.androidx.compose.koinViewModel
import com.neilb.nonocraft.common.Constants
import com.neilb.nonocraft.common.buildGoogleCredentialRequest
import com.neilb.nonocraft.common.openInCustomTab
import com.neilb.nonocraft.common.showToast
import com.neilb.nonocraft.common.signInWithGoogle
import com.neilb.nonocraft.presentation.screens.auth.AuthViewModel
import com.neilb.nonocraft.presentation.screens.auth.components.*
import com.neilb.nonocraft.presentation.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuthGatewayScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onGoogleLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val request = remember { buildGoogleCredentialRequest() }
    val authState by authViewModel.state.collectAsState()

    val legalText = buildAnnotatedString {
        withStyle(SpanStyle(color = TextSecondary)) {
            append("Devam ederek ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "PRIVACY",
                linkInteractionListener = {
                    context.openInCustomTab(Constants.PRIVACY_POLICY_URL)
                }
            )
        ) {
            withStyle(SpanStyle(color = NeonCyan, fontWeight = FontWeight.SemiBold)) {
                append("Gizlilik Politikası")
            }
        }
        withStyle(SpanStyle(color = TextSecondary)) {
            append(" ve ")
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = "TERMS",
                linkInteractionListener = {
                    context.openInCustomTab(Constants.TERMS_OF_SERVICE_URL)
                }
            )
        ) {
            withStyle(SpanStyle(color = NeonCyan, fontWeight = FontWeight.SemiBold)) {
                append("Kullanım Koşulları")
            }
        }
        withStyle(SpanStyle(color = TextSecondary)) {
            append("'nı kabul etmiş olursunuz.")
        }
    }

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) onGoogleLoginSuccess()
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
                .padding(horizontal = 28.dp)
                .padding(bottom = 24.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                AuthLogoRow(modifier = Modifier.padding(bottom = 32.dp))

                Text(
                    text = "Hemen Başla",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Topluluğa katıl, tasarla ve keşfet.",
                    color = TextSecondary,
                    fontSize = 15.sp,
                    fontFamily = AppFont,
                )
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoogleSignInButton(
                    label = "Google ile devam et",
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

                AuthDivider(label = "veya")

                PrimaryAuthButton(
                    text = "Kayıt Ol",
                    onClick = onNavigateToRegister,
                )
                Spacer(Modifier.height(12.dp))

                OutlineAuthButton(
                    text = "Zaten hesabım var",
                    onClick = onNavigateToLogin,
                )
                Spacer(Modifier.height(12.dp))

                GhostAuthButton(
                    text = "Misafir olarak devam et",
                    onClick = onContinueAsGuest,
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    text = legalText,
                    fontSize = 11.sp,
                    fontFamily = AppFont,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}