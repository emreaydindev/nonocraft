package com.neilb.nonocraft.presentation.screens.gameplay

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GameScreen(
    gameId: String
) {
    Column {
        Text("This is Game Screen with id : $gameId")
    }
}