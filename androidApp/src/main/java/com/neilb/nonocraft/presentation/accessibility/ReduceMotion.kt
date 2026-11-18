package com.neilb.nonocraft.presentation.accessibility

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * True when the system "Remove animations" setting is on (animator duration scale 0). Compose's own
 * animations already finish instantly then; this is for looping demos driven by coroutines (tutorial hand,
 * onboarding sample) and pulses, which keep moving otherwise. Matches iOS `accessibilityReduceMotion`.
 */
@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
}
