package com.neilb.nonocraft.presentation.screens.auth.register

import androidx.compose.ui.semantics.contentDescription
import com.neilb.nonocraft.presentation.text.toMessage
import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import com.neilb.nonocraft.presentation.text.ErrorContext
import com.neilb.nonocraft.presentation.text.message
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.neilb.nonocraft.presentation.screens.auth.components.AuthBackRow
import com.neilb.nonocraft.presentation.screens.auth.components.AuthDivider
import com.neilb.nonocraft.presentation.screens.auth.components.AuthPasswordField
import com.neilb.nonocraft.presentation.screens.auth.components.AuthTextField
import com.neilb.nonocraft.presentation.screens.auth.components.GoogleSignInButton
import com.neilb.nonocraft.presentation.screens.auth.components.InfoBanner
import com.neilb.nonocraft.presentation.screens.auth.components.PasswordRequirementRow
import com.neilb.nonocraft.presentation.screens.auth.components.PasswordStrengthBar
import com.neilb.nonocraft.presentation.screens.auth.components.PrimaryAuthButton
import com.neilb.nonocraft.presentation.screens.auth.components.rippleClickable
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
    viewModel: RegisterViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
) {
    val validDescription = stringResource(R.string.auth_valid)
    val state by viewModel.state.collectAsState()
    val authState by authViewModel.state.collectAsState()
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

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
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
                    text = stringResource(R.string.auth_register_title),
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(32.dp))

                GoogleSignInButton(
                    label = stringResource(R.string.auth_sign_up_with_google),
                    onClick = {
                        scope.launch {
                            authViewModel.changeLoadingState(true)
                            signInWithGoogle(context, credentialManager, request)
                                .onSuccess { idToken -> authViewModel.googleLogin(idToken) }
                                .onFailure { e ->
                                    context.showToast(ErrorCode.GOOGLE_SIGN_IN_FAILED.toMessage(context))
                                    authViewModel.changeLoadingState(false)
                                }
                        }
                    },
                    isLoading = authState.isLoading
                )

                AuthDivider(label = stringResource(R.string.auth_or_with_email))

                if (state.error != null) {
                    InfoBanner(
                        message = stringResource(R.string.auth_register_failed),
                        subMessage = state.error!!.message(ErrorContext.REGISTER),
                        isError = true,
                        modifier = Modifier.padding(bottom = 14.dp),
                    )
                }

                AuthTextField(
                    value = username,
                    onValueChange = { username = it.trim().lowercase() },
                    label = stringResource(R.string.auth_username),
                    placeholder = stringResource(R.string.auth_username_placeholder),
                    isError = state.error != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Next,
                    ),
                    trailingIcon = if (username.isNotEmpty() && state.error == null) {
                        { Text("✓", color = NeonCyan, fontSize = 15.sp, modifier = Modifier.clearAndSetSemantics { contentDescription = validDescription }) }
                    } else null,
                )
                Spacer(Modifier.height(12.dp))

                AuthTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = stringResource(R.string.auth_email),
                    placeholder = stringResource(R.string.auth_email_placeholder),
                    isError = !isEmailValid,
                    errorMessage = if (!isEmailValid) stringResource(R.string.auth_email_invalid) else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                )
                Spacer(Modifier.height(12.dp))

                AuthPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.auth_password),
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
                                text = stringResource(R.string.auth_requirements),
                                color = TextMuted,
                                fontSize = 11.sp,
                                letterSpacing = 0.8.sp,
                                fontFamily = AppFont,
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                            PasswordRequirementRow(stringResource(R.string.auth_req_min_length),   met = isPasswordLongEnough)
                            PasswordRequirementRow(stringResource(R.string.auth_req_uppercase), met = isPasswordHasUpper)
                            PasswordRequirementRow(stringResource(R.string.auth_req_digit),      met = isPasswordHasDigit)
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
                    text = stringResource(R.string.auth_sign_up),
                    enabled = canSubmit,
                    isLoading = state.isLoading,
                    onClick = {
                        viewModel.register(email, username, password)
                    },
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextSecondary)) { append(stringResource(R.string.auth_have_account_prefix)) }
                        withStyle(SpanStyle(color = NeonCyan, fontWeight = FontWeight.SemiBold)) { append(stringResource(R.string.auth_sign_in)) }
                    },
                    fontSize = 13.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.rippleClickable(onNavigateToLogin),
                )

                Spacer(Modifier.height(20.dp))

                val legalText = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append(stringResource(R.string.auth_legal_prefix))
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
                            append(stringResource(R.string.common_privacy_policy))
                        }
                    }
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append(stringResource(R.string.auth_legal_and))
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
                            append(stringResource(R.string.common_terms))
                        }
                    }
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append(stringResource(R.string.auth_legal_suffix))
                    }
                }

                Text(
                    text = legalText,
                    fontSize = 11.sp,
                    fontFamily = AppFont,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

            }
        }
    }
}