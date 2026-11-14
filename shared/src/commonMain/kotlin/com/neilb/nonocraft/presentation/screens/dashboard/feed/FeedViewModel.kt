package com.neilb.nonocraft.presentation.screens.dashboard.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val error: String? = null,
    val posts: List<PostItem> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val commentsForActivePost: List<Comment> = emptyList(),
    val activeCommentPostId: String? = null,
    val activeMenuPostId: String? = null,
    val activePlaylistPostId: String? = null
)

enum class FeedTab(val label: String) {
    FEATURED("Öne Çıkanlar"),
    LATEST("En Yeniler"),
    FOLLOWING("Takip Edilenler"),
    MOST_LIKED("En Beğenilenler")
}

enum class TimeRange(val label: String) {
    TODAY("Son 24 Saat"),
    WEEK("Bu Hafta"),
    MONTH("Bu Ay"),
    ALL_TIME("Tüm Zamanlar")
}

data class Comment(
    val id: String,
    val author: String,
    val content: String,
    val timeAgo: String,
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
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> { /* already handled */ }
            }
            
            // Still using mock for playlists for now as we don't have UseCase yet
            _uiState.update { it.copy(playlists = getPlaylistsMock()) }
        }
    }

    private fun getPlaylistsMock(): List<Playlist> {
        return listOf(
            Playlist("pl_1", "Favorilerim", 12),
            Playlist("pl_2", "Zor Bulmacalar", 5),
            Playlist("pl_3", "Piksel Sanatı", 28)
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
