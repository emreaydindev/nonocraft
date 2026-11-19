package com.neilb.nonocraft.presentation.screens.profile

import androidx.lifecycle.ViewModel
import com.neilb.nonocraft.presentation.model.epochMillisAgo
import com.neilb.nonocraft.presentation.model.Difficulty
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
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
                title = "Night Skyline",
                author = userId,
                createdAtEpochMillis = epochMillisAgo(3.days),
                likes = 45,
                comments = 7,
                isLiked = false,
                difficulty = Difficulty.HARD,
                size = 8,
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
                title = "Star",
                author = userId,
                createdAtEpochMillis = epochMillisAgo(7.days),
                likes = 23,
                comments = 3,
                isLiked = true,
                difficulty = Difficulty.EASY,
                size = 5,
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
            Playlist("pl_1", "My Favourites", 12),
            Playlist("pl_2", "Hard Puzzles", 5),
            Playlist("pl_3", "Pixel Art", 28)
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
            Comment("c1", "zeynep", "The colours work so well together!", epochMillisAgo(1.hours), false),
            Comment("c2", "burak", "Had a lot of fun solving this.", epochMillisAgo(30.minutes), true)
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