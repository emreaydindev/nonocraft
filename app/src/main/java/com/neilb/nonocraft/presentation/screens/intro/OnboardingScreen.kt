package com.neilb.nonocraft.presentation.screens.intro

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    Column {
        Text("This is Onboarding Screen")
        Button(onClick = onFinishOnboarding) {
            Text("Get Started")
        }
    }
}