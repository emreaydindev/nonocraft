package com.neilb.nonocraft.presentation.navigation

import androidx.lifecycle.ViewModel
import com.neilb.nonocraft.data.local.TokenManager

class AppViewModel(
    private val tokenManager: TokenManager,
) : ViewModel() {
    val hasSeenTutorial: Boolean
        get() = tokenManager.hasSeenTutorial

    fun completeOnboarding() {
        tokenManager.hasCompletedOnboarding = true
    }

    fun completeTutorial() {
        tokenManager.hasSeenTutorial = true
    }

    fun enterGuestMode() {
        tokenManager.setGuestMode()
    }
}