package com.neilb.nonocraft.presentation.screens.dashboard.library.detail

import androidx.lifecycle.SavedStateHandle
import com.neilb.nonocraft.presentation.model.epochMillisAgo
import com.neilb.nonocraft.presentation.model.Difficulty
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration
import androidx.lifecycle.ViewModel
import com.neilb.nonocraft.presentation.components.PostAction
import com.neilb.nonocraft.presentation.model.PostItem
import com.neilb.nonocraft.presentation.screens.dashboard.feed.Comment
import com.neilb.nonocraft.presentation.screens.dashboard.feed.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

/** Which library list this is; the UI shows the localized title. */
enum class LibraryCategory { LIKED, DOWNLOADED, SHARED, DRAFTS, COMPLETED, DAILIES, PLAYLIST }

data class LibraryDetailUiState(
    val category: LibraryCategory = LibraryCategory.PLAYLIST,
    /** The user's own playlist name when [category] is [LibraryCategory.PLAYLIST] and it is known. */
    val playlistName: String? = null,
    val posts: List<PostItem> = emptyList(),
    val isLoading: Boolean = true,
    val playlists: List<Playlist> = emptyList(),
    val commentsForActivePost: List<Comment> = emptyList(),
    val activeCommentPostId: String? = null,
    val activeMenuPostId: String? = null,
    val activePlaylistPostId: String? = null
)

class LibraryDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Same key as the `categoryId` property of the navigation route.
    private val categoryId: String = savedStateHandle["categoryId"] ?: ""

    private val _uiState = MutableStateFlow(LibraryDetailUiState())
    val uiState: StateFlow<LibraryDetailUiState> = _uiState.asStateFlow()

    init {
        loadCategoryData()
    }

    private fun loadCategoryData() {
        val (category, mockPosts) = when (categoryId) {
            "liked" -> LibraryCategory.LIKED to getMockLikedPosts()
            "downloaded" -> LibraryCategory.DOWNLOADED to getMockDownloadedPosts()
            "shared" -> LibraryCategory.SHARED to getMockSharedPosts()
            "drafts" -> LibraryCategory.DRAFTS to getMockDraftPosts()
            "completed" -> LibraryCategory.COMPLETED to getMockCompletedPosts()
            "dailies" -> LibraryCategory.DAILIES to getMockDailyPosts()
            else -> LibraryCategory.PLAYLIST to getMockLikedPosts()
        }

        val mockPlaylists = listOf(
            Playlist("pl_1", "My Favourites", 12),
            Playlist("pl_2", "Hard Puzzles", 5),
            Playlist("pl_3", "Pixel Art", 28)
        )

        _uiState.update {
            it.copy(
                category = category,
                posts = mockPosts,
                playlists = mockPlaylists,
                isLoading = false
            )
        }
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
            Comment("c1", "zeynep", "Such a creative design!", epochMillisAgo(1.hours), false),
            Comment("c2", "burak", "Really enjoyed solving this, thanks.", epochMillisAgo(30.minutes), true)
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
        _uiState.update { it.copy(activePlaylistPostId = null) }
    }

    fun downloadPuzzle(postId: String) {
        closeMenu()
    }

    private fun getMockLikedPosts(): List<PostItem> = listOf(
        PostItem(
            id = "liked_1", title = "Heart", author = "ali", createdAtEpochMillis = epochMillisAgo(5.hours), likes = 88, comments = 4,
            isLiked = true, difficulty = Difficulty.EASY, size = 5, accentColor = 0xFFFF4444,
            pattern = listOf(
                listOf(0, 1, 0, 1, 0), listOf(1, 1, 1, 1, 1), listOf(1, 1, 1, 1, 1),
                listOf(0, 1, 1, 1, 0), listOf(0, 0, 1, 0, 0)
            )
        ),
        PostItem(
            id = "liked_2", title = "Star", author = "zeynep", createdAtEpochMillis = epochMillisAgo(1.days), likes = 150, comments = 22,
            isLiked = true, difficulty = Difficulty.MEDIUM, size = 5, accentColor = 0xFFFFD700,
            pattern = listOf(
                listOf(0, 0, 1, 0, 0), listOf(0, 1, 1, 1, 0), listOf(1, 1, 1, 1, 1),
                listOf(0, 1, 1, 1, 0), listOf(0, 0, 1, 0, 0)
            )
        )
    )

    private fun getMockDownloadedPosts(): List<PostItem> = listOf(
        PostItem(
            id = "dl_1", title = "Night Skyline", author = "neilb", createdAtEpochMillis = epochMillisAgo(3.days), likes = 45, comments = 7,
            isLiked = false, difficulty = Difficulty.HARD, size = 8, accentColor = 0xFF00FFCC,
            pattern = listOf(
                listOf(1, 1, 1, 1, 1, 1, 1, 1), listOf(1, 0, 0, 0, 0, 0, 0, 1),
                listOf(1, 0, 1, 1, 1, 1, 0, 1), listOf(1, 0, 1, 0, 0, 1, 0, 1),
                listOf(1, 0, 1, 0, 0, 1, 0, 1), listOf(1, 0, 1, 1, 1, 1, 0, 1),
                listOf(1, 0, 0, 0, 0, 0, 0, 1), listOf(1, 1, 1, 1, 1, 1, 1, 1)
            )
        )
    )

    private fun getMockSharedPosts(): List<PostItem> = listOf(
        PostItem(
            id = "shared_1", title = "Sword", author = "your_username", createdAtEpochMillis = epochMillisAgo(7.days), likes = 12, comments = 1,
            isLiked = false, difficulty = Difficulty.EASY, size = 5, accentColor = 0xFFAAAAAA,
            pattern = listOf(
                listOf(0, 0, 0, 0, 1), listOf(0, 0, 0, 1, 0), listOf(0, 0, 1, 0, 0),
                listOf(0, 1, 0, 0, 0), listOf(1, 0, 0, 0, 0)
            )
        )
    )

    private fun getMockDraftPosts(): List<PostItem> = listOf(
        PostItem(
            id = "draft_1", title = "Mushroom (draft)", author = "your_username", createdAtEpochMillis = epochMillisAgo(Duration.ZERO), likes = 0, comments = 0,
            isLiked = false, difficulty = null, size = 6, accentColor = 0xFF4CAF50,
            pattern = listOf(
                listOf(0, 1, 1, 1, 1, 0), listOf(1, 1, 1, 1, 1, 1), listOf(1, 0, 1, 1, 0, 1),
                listOf(1, 1, 1, 1, 1, 1), listOf(0, 0, 1, 1, 0, 0), listOf(0, 0, 1, 1, 0, 0)
            )
        )
    )

    private fun getMockCompletedPosts(): List<PostItem> = listOf(
        PostItem(
            id = "comp_1", title = "Cat", author = "system", createdAtEpochMillis = epochMillisAgo(30.days), likes = 890, comments = 120,
            isLiked = true, difficulty = Difficulty.MEDIUM, size = 5, accentColor = 0xFFFF9800,
            pattern = listOf(
                listOf(1, 0, 0, 0, 1), listOf(1, 1, 0, 1, 1), listOf(1, 1, 1, 1, 1),
                listOf(0, 1, 1, 1, 0), listOf(0, 1, 0, 1, 0)
            )
        )
    )

    private fun getMockDailyPosts(): List<PostItem> = listOf(
        PostItem(
            id = "daily_1", title = "Sun", author = "nonocraft_daily", createdAtEpochMillis = epochMillisAgo(12.hours), likes = 4500, comments = 300,
            isLiked = true, difficulty = Difficulty.HARD, size = 5, accentColor = 0xFFFFEB3B,
            pattern = listOf(
                listOf(0, 1, 0, 1, 0), listOf(1, 1, 1, 1, 1), listOf(0, 1, 1, 1, 0),
                listOf(1, 1, 1, 1, 1), listOf(0, 1, 0, 1, 0)
            )
        )
    )
}