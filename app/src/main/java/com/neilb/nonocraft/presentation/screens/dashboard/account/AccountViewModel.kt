package com.neilb.nonocraft.presentation.screens.dashboard.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AccountUiState(
    val isGuest: Boolean = false,
    val username: String = "",
    val bio: String = "Nonocraft Bulmaca Meraklısı",
    val avatarUrl: String? = null,
    // TODO: solvedCount/followers/following -> GetUserStats use case gelince eklenecek
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    sessionManager: SessionManager,
) : ViewModel() {

    val uiState: StateFlow<AccountUiState> = sessionManager.userProfile
        .map { profile ->
            if (profile != null) {
                AccountUiState(
                    isGuest = false,
                    username = "@${profile.username}",
                    avatarUrl = profile.avatarUrl,
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