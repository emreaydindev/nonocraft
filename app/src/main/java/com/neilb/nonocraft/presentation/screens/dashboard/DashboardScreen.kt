package com.neilb.nonocraft.presentation.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.neilb.nonocraft.presentation.navigation.Route

@Composable
fun DashboardScreen(
    onNavigateToGame: (gameId: String) -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column {
        Text("This is Dashboard Screen")
        Button(onClick = { onNavigateToGame("GameId") }) {
            Text("Go to Game")
        }
        Button(onClick = onNavigateToPremium) {
            Text("Go to Premium")
        }
        Button(onClick = onNavigateToSettings) {
            Text("Go to Settings")
        }
    }
}