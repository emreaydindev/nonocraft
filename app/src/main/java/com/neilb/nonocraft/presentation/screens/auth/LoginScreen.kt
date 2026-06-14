package com.neilb.nonocraft.presentation.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.neilb.nonocraft.presentation.navigation.Route

@Composable
fun LoginScreen(
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    Column {
        Text("This is Login Screen")
        Button(onClick = onNavigateToForgotPassword) {
            Text("Forgot Password")
        }
        Button(onClick = onLoginSuccess) {
            Text("Login")
        }
    }
}