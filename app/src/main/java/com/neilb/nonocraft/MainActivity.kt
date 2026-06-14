package com.neilb.nonocraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.neilb.nonocraft.presentation.navigation.NavigationView
import com.neilb.nonocraft.presentation.theme.NonogramTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    MaterialTheme.colorScheme.primary.toArgb(),
                    MaterialTheme.colorScheme.primary.toArgb(),
                ),
                navigationBarStyle = SystemBarStyle.auto(
                    MaterialTheme.colorScheme.primary.toArgb(),
                    MaterialTheme.colorScheme.primary.toArgb()
                )
            )
            NonogramTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationView()
                }
            }
        }
    }
}