package com.neilb.nonocraft.presentation.screens.profile

import androidx.lifecycle.ViewModel
import com.neilb.nonocraft.presentation.components.PostAction
import com.neilb.nonocraft.presentation.model.PostItem
import com.neilb.nonocraft.presentation.screens.dashboard.feed.Comment
import com.neilb.nonocraft.presentation.screens.dashboard.feed.FeedUiState
import com.neilb.nonocraft.presentation.screens.dashboard.feed.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    fun loadUserPosts(userId: String) {
        val mockPosts = listOf(
            PostItem(
                id = "${userId}_post_1",
                title = "Gece Manzarası",
                author = userId,
                timeAgo = "3g önce",
                likes = 45,
                comments = 7,
                isLiked = false,
                difficulty = "Zor",
                size = "8×8",
                accentColor = 0xFF00FFCC,
                pattern = listOf(
                    listOf(1, 1, 1, 1, 1, 1, 1, 1),
                    listOf(1, 0, 0, 0, 0, 0, 0, 1),
                    listOf(1, 0, 1, 1, 1, 1, 0, 1),
                    listOf(1, 0, 1, 0, 0, 1, 0, 1),
                    listOf(1, 0, 1, 0, 0, 1, 0, 1),
                    listOf(1, 0, 1, 1, 1, 1, 0, 1),
                    listOf(1, 0, 0, 0, 0, 0, 0, 1),
                    listOf(1, 1, 1, 1, 1, 1, 1, 1)
                )
            ),
            PostItem(
                id = "${userId}_post_2",
                title = "Yıldız",
                author = userId,
                timeAgo = "1h önce",
                likes = 23,
                comments = 3,
                isLiked = true,
                difficulty = "Kolay",
                size = "5×5",
                accentColor = 0xFFFFD700,
                pattern = listOf(
                    listOf(0, 0, 1, 0, 0),
                    listOf(0, 1, 1, 1, 0),
                    listOf(1, 1, 1, 1, 1),
                    listOf(0, 1, 1, 1, 0),
                    listOf(0, 0, 1, 0, 0)
                )
            )
        )

        val mockPlaylists = listOf(
            Playlist("pl_1", "Favorilerim", 12),
            Playlist("pl_2", "Zor Bulmacalar", 5),
            Playlist("pl_3", "Piksel Sanatı", 28)
        )

        _uiState.update { it.copy(posts = mockPosts, playlists = mockPlaylists) }
    }

    fun handlePostAction(action: PostAction) {
        when (action) {
            is PostAction.LikeClick -> toggleLike(action.postId)
            is PostAction.CommentClick -> openComments(action.postId)
            is PostAction.MoreClick -> openMenu(action.postId)
            is PostAction.FollowClick -> toggleFollow(action.postId)
            else -> {}
        }
    }

    private fun toggleFollow(postId: String) {
        _uiState.update { state ->
            val updatedPosts = state.posts.map { post ->
                if (post.id == postId) post.copy(isFollowed = !post.isFollowed) else post
            }
            state.copy(posts = updatedPosts)
        }
    }

    private fun toggleLike(postId: String) {
        _uiState.update { state ->
            val updatedPosts = state.posts.map { post ->
                if (post.id == postId) {
                    val newLikeStatus = !post.isLiked
                    val newLikeCount = if (newLikeStatus) post.likes + 1 else post.likes - 1
                    post.copy(isLiked = newLikeStatus, likes = newLikeCount)
                } else {
                    post
                }
            }
            state.copy(posts = updatedPosts)
        }
    }

    fun openComments(postId: String) {
        val mockComments = listOf(
            Comment("c1", "zeynep", "Renk uyumu harika olmuş!", "1s önce", false),
            Comment("c2", "burak", "Bunu çözerken çok eğlendim.", "30d önce", true)
        )
        _uiState.update { it.copy(activeCommentPostId = postId, commentsForActivePost = mockComments) }
    }

    fun closeComments() {
        _uiState.update { it.copy(activeCommentPostId = null, commentsForActivePost = emptyList()) }
    }

    fun openMenu(postId: String) {
        _uiState.update { it.copy(activeMenuPostId = postId) }
    }

    fun closeMenu() {
        _uiState.update { it.copy(activeMenuPostId = null) }
    }

    fun openPlaylistDialog() {
        val currentPostId = _uiState.value.activeMenuPostId
        if (currentPostId != null) {
            _uiState.update { it.copy(activeMenuPostId = null, activePlaylistPostId = currentPostId) }
        }
    }

    fun closePlaylistDialog() {
        _uiState.update { it.copy(activePlaylistPostId = null) }
    }

    fun createAndAddToPlaylist(name: String) {
        val newPlaylist = Playlist(id = "pl_${Random.nextInt(Int.MAX_VALUE)}", name = name, postCount = 1)
        _uiState.update { state ->
            state.copy(
                playlists = state.playlists + newPlaylist,
                activePlaylistPostId = null
            )
        }
    }

    fun addToPlaylist(playlistId: String) {
        _uiState.update { it.copy(activePlaylistPostId = playlistId) }
    }

    fun downloadPuzzle(postId: String) {
        //TODO: download puzzle
    }
}