package com.neilb.nonocraft.presentation.screens.dashboard.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class PlaylistItem(
    val id: String,
    val name: String,
    val postCount: Int,
    /** ARGB, e.g. 0xFFE24B4A. */
    val coverColor: Long
)

data class LibraryUiState(
    val likedArtsCount: Int = 0,
    val downloadedLevelsCount: Int = 0,
    val sharedArtsCount: Int = 0,
    val draftArtsCount: Int = 0,
    val completedLevelsCount: Int = 0,
    val completedDailiesCount: Int = 0,
    val playlists: List<PlaylistItem> = emptyList()
)

sealed interface LibraryAction {
    data class OnCategoryClick(val categoryId: String) : LibraryAction
    data class OnPlaylistClick(val playlistId: String) : LibraryAction
    object OnCreatePlaylist : LibraryAction
    data class OnCreatePlaylistSubmit(val name: String) : LibraryAction
}

sealed interface LibraryUiEvent {
    data class NavigateToCategory(val categoryId: String) : LibraryUiEvent
    data class NavigateToPlaylist(val playlistId: String) : LibraryUiEvent
    object ShowCreatePlaylistDialog : LibraryUiEvent
}

class LibraryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<LibraryUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadLibraryData()
    }

    private fun loadLibraryData() {
        _uiState.update {
            it.copy(
                likedArtsCount = 156,
                downloadedLevelsCount = 12,
                sharedArtsCount = 8,
                draftArtsCount = 3,
                completedLevelsCount = 42,
                completedDailiesCount = 18,
                playlists = listOf(
                    PlaylistItem("pl_1", "Zor Bulmacalar", 15, 0xFFE24B4A),
                    PlaylistItem("pl_2", "Radiohead Dinlerken", 24, 0xFF00FFDC),
                    PlaylistItem("pl_3", "Piksel Sanatı", 8, 0xFFFFD700)
                )
            )
        }
    }

    fun handleAction(action: LibraryAction) {
        when (action) {
            is LibraryAction.OnCategoryClick -> {
                viewModelScope.launch {
                    _uiEvent.send(LibraryUiEvent.NavigateToCategory(action.categoryId))
                }
            }
            is LibraryAction.OnPlaylistClick -> {
                viewModelScope.launch {
                    _uiEvent.send(LibraryUiEvent.NavigateToPlaylist(action.playlistId))
                }
            }
            is LibraryAction.OnCreatePlaylist -> {
                viewModelScope.launch {
                    _uiEvent.send(LibraryUiEvent.ShowCreatePlaylistDialog)
                }
            }
            is LibraryAction.OnCreatePlaylistSubmit -> {
                val newPlaylist = PlaylistItem(
                    id = "pl_${Random.nextInt(Int.MAX_VALUE)}",
                    name = action.name,
                    postCount = 0,
                    coverColor = 0xFFB64FF6
                )
                _uiState.update { state ->
                    state.copy(playlists = state.playlists + newPlaylist)
                }
            }
        }
    }
}