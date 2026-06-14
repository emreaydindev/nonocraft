package com.neilb.nonocraft.presentation.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.neilb.nonocraft.presentation.navigation.Route

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    Column {
        Text("This is Register Screen")
        Button(onClick = onNavigateToLogin) {
            Text("Login")
        }
        Button(onClick = onContinueAsGuest) {
            Text("Continue as Guest")
        }
        Button(onClick = onRegisterSuccess) {
            Text("Register")
        }
    }
}