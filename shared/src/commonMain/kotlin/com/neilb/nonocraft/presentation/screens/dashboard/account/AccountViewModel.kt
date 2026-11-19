package com.neilb.nonocraft.presentation.screens.dashboard.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.session.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AccountUiState(
    val isGuest: Boolean = false,
    val username: String = "",
    /** The user's own bio; null shows the UI's localized placeholder. */
    val bio: String? = null,
    val avatarUrl: String? = null,
    // TODO: solvedCount/followers/following -> GetUserStats use case gelince eklenecek
)

class AccountViewModel(
    private val tokenManager: TokenManager,
    sessionManager: SessionManager,
) : ViewModel() {

    val uiState: StateFlow<AccountUiState> = sessionManager.userProfile
        .map { profile ->
            if (profile != null) {
                AccountUiState(
                    isGuest = false,
                    username = "@${profile.username}",
                    bio = profile.bio?.takeIf { it.isNotBlank() },
                    avatarUrl = profile.avatarPuzzleId,
                )
            } else {
                AccountUiState(isGuest = tokenManager.isGuest)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountUiState(isGuest = tokenManager.isGuest)
        )
}