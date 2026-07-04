package com.neilb.nonocraft.presentation.screens.dashboard.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.neilb.nonocraft.presentation.components.CommentsSheetContent
import com.neilb.nonocraft.presentation.components.PlaylistSelectionDialog
import com.neilb.nonocraft.presentation.components.PostAction
import com.neilb.nonocraft.presentation.components.PostCard
import com.neilb.nonocraft.presentation.components.PostMenuSheetContent
import com.neilb.nonocraft.presentation.screens.dashboard.components.noRippleClickable
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onNavigateToGame: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCrafting: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()
    val isFabExpanded = listState.isScrollingUp()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        GridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Keşfet",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
            )

            FeedFilterRow(
                selectedTab = uiState.selectedTab,
                selectedTimeRange = uiState.selectedTimeRange,
                showTimeRangeDropdown = uiState.showTimeRangeDropdown,
                onTabSelected = { viewModel.selectTab(it) },
                onTimeRangeSelect = { viewModel.selectTimeRange(it) },
                onDismissTimeRange = { viewModel.dismissTimeRangeDropdown() }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 16.dp,
                    bottom = 88.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonCyan)
                        }
                    }
                } else if (uiState.error != null) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(text = uiState.error!!, color = DangerRed, fontFamily = AppFont)
                        }
                    }
                } else if (uiState.posts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Henüz bir bulmaca bulunamadı.", color = TextMuted, fontFamily = AppFont)
                        }
                    }
                } else {
                    items(uiState.posts) { post ->
                        PostCard(
                            post = post,
                            showFollowButton = uiState.selectedTab != FeedTab.FOLLOWING,
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

        ExtendedFloatingActionButton(
            onClick = onNavigateToCrafting,
            expanded = isFabExpanded,
            icon = { Icon(Icons.Filled.Brush, contentDescription = "Oluştur") },
            text = {
                Text(
                    text = "Oluştur",
                    fontFamily = AppFont,
                    fontWeight = FontWeight.Bold
                )
            },
            containerColor = PurpleAccent,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        )

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

@Composable
private fun FeedFilterRow(
    selectedTab: FeedTab,
    selectedTimeRange: TimeRange,
    showTimeRangeDropdown: Boolean,
    onTabSelected: (FeedTab) -> Unit,
    onTimeRangeSelect: (TimeRange) -> Unit,
    onDismissTimeRange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FeedTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val supportsTimeRange = tab == FeedTab.LATEST || tab == FeedTab.MOST_LIKED

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) NeonCyan else SurfaceDark)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) NeonCyan else BorderDark,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .noRippleClickable { onTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) BackgroundDark else TextMuted,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = AppFont
                    )
                    if (supportsTimeRange) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            tint = if (isSelected) BackgroundDark else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (isSelected && supportsTimeRange) {
                    DropdownMenu(
                        expanded = showTimeRangeDropdown,
                        onDismissRequest = onDismissTimeRange,
                        modifier = Modifier
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .width(160.dp)
                    ) {
                        TimeRange.entries.forEach { range ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = range.label,
                                        color = if (range == selectedTimeRange) NeonCyan else TextPrimary,
                                        fontFamily = AppFont,
                                        fontSize = 14.sp,
                                        fontWeight = if (range == selectedTimeRange) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                onClick = { onTimeRangeSelect(range) },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}