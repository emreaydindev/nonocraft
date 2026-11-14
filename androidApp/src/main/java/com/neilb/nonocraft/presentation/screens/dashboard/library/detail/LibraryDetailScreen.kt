package com.neilb.nonocraft.presentation.screens.dashboard.library.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neilb.nonocraft.presentation.components.CommentsSheetContent
import com.neilb.nonocraft.presentation.components.PlaylistSelectionDialog
import com.neilb.nonocraft.presentation.components.PostAction
import com.neilb.nonocraft.presentation.components.PostCard
import com.neilb.nonocraft.presentation.components.PostMenuSheetContent
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDetailScreen(
    viewModel: LibraryDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToGame: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        GridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                }
                Text(
                    text = uiState.title,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonCyan)
                }
            } else if (uiState.posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Burada henüz bir şey yok.",
                        color = TextMuted,
                        fontFamily = AppFont,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 16.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(uiState.posts) { post ->
                        PostCard(
                            post = post,
                            showFollowButton = false,
                            onAction = { action ->
                                when (action) {
                                    is PostAction.PlayClick -> onNavigateToGame(action.postId)
                                    is PostAction.ProfileClick -> onNavigateToProfile(action.author)
                                    else -> viewModel.handlePostAction(action)
                                }
                            }
                        )
                    }
                }
            }
        }

        if (uiState.activeCommentPostId != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeComments() },
                sheetState = sheetState,
                containerColor = BackgroundDark,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .size(32.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(BorderDark)
                    )
                }
            ) {
                CommentsSheetContent(comments = uiState.commentsForActivePost)
            }
        }

        if (uiState.activeMenuPostId != null) {
            val activePost = uiState.posts.find { it.id == uiState.activeMenuPostId }
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeMenu() },
                sheetState = sheetState,
                containerColor = BackgroundDark,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .size(32.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(BorderDark)
                    )
                }
            ) {
                if (activePost != null) {
                    PostMenuSheetContent(
                        post = activePost,
                        onPlay = {
                            viewModel.closeMenu()
                            onNavigateToGame(activePost.id)
                        },
                        onSave = { viewModel.closeMenu() },
                        onAddToPlaylist = { viewModel.openPlaylistDialog() },
                        onGoToProfile = {
                            viewModel.closeMenu()
                            onNavigateToProfile(activePost.author)
                        },
                        onReport = { viewModel.closeMenu() },
                        onDownload = { viewModel.downloadPuzzle(activePost.id) }
                    )
                }
            }
        }

        if (uiState.activePlaylistPostId != null) {
            PlaylistSelectionDialog(
                playlists = uiState.playlists,
                onDismiss = { viewModel.closePlaylistDialog() },
                onSelectPlaylist = { viewModel.addToPlaylist(it) },
                onCreatePlaylist = { viewModel.createAndAddToPlaylist(it) }
            )
        }
    }
}