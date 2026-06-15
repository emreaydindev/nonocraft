package com.neilb.nonocraft.presentation.screens.auth.gateway

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.screens.auth.components.*
import com.neilb.nonocraft.presentation.theme.*

@Composable
fun AuthGatewayScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onContinueAsGuest: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
    ) {
        AuthGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {

            AuthLogoRow(modifier = Modifier.padding(bottom = 36.dp))

            Text(
                text = "Hemen Başla",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Topluluğa katıl, tasarla ve keşfet.",
                color = TextSecondary,
                fontSize = 13.sp,
                fontFamily = AppFont,
            )

            Spacer(Modifier.height(32.dp))

            GoogleSignInButton(
                label = "Google ile devam et",
                onClick = {
                    // TODO: Google OAuth akışını başlat
                },
            )

            AuthDivider(label = "veya")

            PrimaryAuthButton(
                text = "Kayıt Ol",
                onClick = onNavigateToRegister,
            )
            Spacer(Modifier.height(10.dp))

            OutlineAuthButton(
                text = "Zaten hesabım var",
                onClick = onNavigateToLogin,
            )
            Spacer(Modifier.height(8.dp))

            GhostAuthButton(
                text = "Misafir olarak devam et",
                onClick = onContinueAsGuest,
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextMuted)) {
                        append("Devam ederek ")
                    }
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append("Gizlilik Politikası")
                    }
                    withStyle(SpanStyle(color = TextMuted)) {
                        append(" ve ")
                    }
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append("Kullanım Koşulları")
                    }
                    withStyle(SpanStyle(color = TextMuted)) {
                        append("'nı kabul etmiş olursunuz.")
                    }
                },
                fontSize = 10.sp,
                fontFamily = AppFont,
                lineHeight = 15.sp,
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}