package com.neilb.nonocraft.presentation.screens.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashDestination {
    data object Loading : SplashDestination
    data object Onboarding : SplashDestination
    data object AuthGateway : SplashDestination
    data object Dashboard : SplashDestination
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            _destination.value = when {
                !tokenManager.hasCompletedOnboarding -> SplashDestination.Onboarding

                tokenManager.isLoggedIn -> {
                    val sessionValid = sessionManager.refreshSession()
                    if (sessionValid) SplashDestination.Dashboard else SplashDestination.AuthGateway
                }

                tokenManager.isGuest -> SplashDestination.Dashboard

                else -> SplashDestination.AuthGateway
            }
        }
    }
}