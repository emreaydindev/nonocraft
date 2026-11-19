package com.neilb.nonocraft.presentation.screens.dashboard.feed

import androidx.lifecycle.ViewModel
import com.neilb.nonocraft.presentation.model.epochMillisAgo
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.use_case.puzzle.GetPuzzles
import com.neilb.nonocraft.presentation.components.PostAction
import com.neilb.nonocraft.presentation.mapper.toPostItem
import com.neilb.nonocraft.presentation.model.PostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import kotlinx.coroutines.launch

data class FeedUiState(
    val selectedTab: FeedTab = FeedTab.FEATURED,
    val selectedTimeRange: TimeRange = TimeRange.ALL_TIME,
    val showTimeRangeDropdown: Boolean = false,
    val isLoading: Boolean = false,
    val error: ErrorCode? = null,
    val posts: List<PostItem> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val commentsForActivePost: List<Comment> = emptyList(),
    val activeCommentPostId: String? = null,
    val activeMenuPostId: String? = null,
    val activePlaylistPostId: String? = null
)

enum class FeedTab { FEATURED, LATEST, FOLLOWING, MOST_LIKED }

enum class TimeRange { TODAY, WEEK, MONTH, ALL_TIME }

data class Comment(
    val id: String,
    val author: String,
    val content: String,
    /** When it was posted; the UI formats it as relative time. */
    val createdAtEpochMillis: Long?,
    val isLiked: Boolean
)

data class Playlist(
    val id: String,
    val name: String,
    val postCount: Int
)

private const val COMMUNITY_TYPE = "community"

class FeedViewModel(
    private val getPuzzles: GetPuzzles
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // TODO: every tab shows the newest community puzzles for now. The dedicated /feed
            // endpoints do not return the grid that the card preview needs.
            when (val result = getPuzzles(type = COMMUNITY_TYPE)) {
                is Resource.Success -> {
                    val postItems = result.data?.map { it.toPostItem() } ?: emptyList()
                    _uiState.update { it.copy(isLoading = false, posts = postItems) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error.code) }
                }
                is Resource.Loading -> { /* already handled */ }
            }
            
            // Still using mock for playlists for now as we don't have UseCase yet
            _uiState.update { it.copy(playlists = getPlaylistsMock()) }
        }
    }

    private fun getPlaylistsMock(): List<Playlist> {
        return listOf(
            Playlist("pl_1", "My Favourites", 12),
            Playlist("pl_2", "Hard Puzzles", 5),
            Playlist("pl_3", "Pixel Art", 28)
        )
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

    fun selectTab(tab: FeedTab) {
        val currentState = _uiState.value
        val supportsTimeRange = tab == FeedTab.LATEST || tab == FeedTab.MOST_LIKED
        if (tab == currentState.selectedTab && supportsTimeRange) {
            _uiState.update { it.copy(showTimeRangeDropdown = !it.showTimeRangeDropdown) }
        } else {
            _uiState.update { it.copy(selectedTab = tab, showTimeRangeDropdown = false) }
            loadData() // Reload on tab change
        }
    }

    fun selectTimeRange(range: TimeRange) {
        _uiState.update { it.copy(selectedTimeRange = range, showTimeRangeDropdown = false) }
        loadData() // Reload on time range change
    }

    fun dismissTimeRangeDropdown() {
        _uiState.update { it.copy(showTimeRangeDropdown = false) }
    }

    fun toggleFollow(postId: String) {
        _uiState.update { state ->
            val updatedPosts = state.posts.map { post ->
                if (post.id == postId) post.copy(isFollowed = !post.isFollowed) else post
            }
            state.copy(posts = updatedPosts)
        }
    }

    fun toggleLike(postId: String) {
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
