package com.neilb.nonocraft.presentation.screens.dashboard.playground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.session.SessionManager
import com.neilb.nonocraft.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaygroundViewModel(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaygroundUiState())
    val uiState: StateFlow<PlaygroundUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadCalendarData()
        loadLevelsData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(userSection = SectionState.Loading) }
            if (tokenManager.isGuest) {
                _uiState.update { it.copy(userSection = SectionState.Success(User(
                    id = "guest",
                    email = "",
                    username = "",
                    bio = null,
                    avatarPuzzleId = null,
                    isPremium = false,
                    emailVerified = false,
                    stars = 0
                ))) }
            } else {

            }
        }
    }

    private fun loadCalendarData() {

    }

    private fun loadLevelsData() {

    }

}