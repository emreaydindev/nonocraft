package com.neilb.nonocraft.presentation.screens.intro

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit
) {
    Column {
        Text("This is Splash Screen")
        Button(onClick = onNavigateToOnboarding) {
            Text("Go to Onboarding")
        }
    }
}