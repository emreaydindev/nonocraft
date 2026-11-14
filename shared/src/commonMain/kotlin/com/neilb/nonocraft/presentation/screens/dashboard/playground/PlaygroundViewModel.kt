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
        observeUser()
    }

    fun onEvent(event: PlaygroundUiEvent) {
        when (event) {
            PlaygroundUiEvent.Refresh -> refresh()
        }
    }

    private fun observeUser() {
        if (tokenManager.isGuest) {
            _uiState.update { it.copy(userSection = SectionState.Success(GUEST_USER)) }
            return
        }
        viewModelScope.launch {
            sessionManager.userProfile.collect { user ->
                _uiState.update {
                    it.copy(userSection = if (user != null) SectionState.Success(user) else SectionState.Loading)
                }
            }
        }
    }

    private fun refresh() {
        if (tokenManager.isGuest) return
        viewModelScope.launch {
            if (!sessionManager.refreshSession()) {
                _uiState.update { it.copy(userSection = SectionState.Error("Profil yüklenemedi")) }
            }
        }
    }

    private companion object {
        val GUEST_USER = User(
            id = "guest",
            email = "",
            username = "Misafir",
            bio = null,
            avatarPuzzleId = null,
            isPremium = false,
            emailVerified = false,
            stars = 0,
            streakCount = 0,
            solvedPuzzles = 0,
            solvedDailies = 0,
            followersCount = 0,
            followingCount = 0,
            postsCount = 0,
        )
    }
}
