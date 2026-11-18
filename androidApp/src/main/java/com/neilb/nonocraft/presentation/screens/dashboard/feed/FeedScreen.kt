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
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.neilb.nonocraft.presentation.adaptive.ListPaneWidth
import com.neilb.nonocraft.presentation.adaptive.ReadableWidth
import com.neilb.nonocraft.presentation.adaptive.isWideWindow
import com.neilb.nonocraft.presentation.components.PostRow
import com.neilb.nonocraft.presentation.model.PostItem
import com.neilb.nonocraft.presentation.components.CommentsSheetContent
import com.neilb.nonocraft.presentation.components.PlaylistSelectionDialog
import com.neilb.nonocraft.presentation.components.PostAction
import com.neilb.nonocraft.presentation.components.PostCard
import com.neilb.nonocraft.presentation.components.PostMenuSheetContent
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
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
    viewModel: FeedViewModel = koinViewModel(),
    onNavigateToGame: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCrafting: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()
    // Post shown in the preview pane of the two-pane layout; the first post until one is picked.
    var selectedPostId by rememberSaveable { mutableStateOf<String?>(null) }
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

            val filterRow: @Composable () -> Unit = {
                FeedFilterRow(
                    selectedTab = uiState.selectedTab,
                    selectedTimeRange = uiState.selectedTimeRange,
                    showTimeRangeDropdown = uiState.showTimeRangeDropdown,
                    onTabSelected = { viewModel.selectTab(it) },
                    onTimeRangeSelect = { viewModel.selectTimeRange(it) },
                    onDismissTimeRange = { viewModel.dismissTimeRangeDropdown() }
                )
            }
            val onPostAction: (PostAction) -> Unit = { action ->
                when (action) {
                    is PostAction.PlayClick -> onNavigateToGame(action.postId)
                    is PostAction.ProfileClick -> onNavigateToProfile(action.author)
                    else -> viewModel.handlePostAction(action)
                }
            }

            if (isWideWindow()) {
                // Tablets, unfolded foldables and wide windows: post list on the left, the picked post's
                // card on the right (same as the iOS two-pane Explore).
                val selectedPost = uiState.posts.firstOrNull { it.id == selectedPostId } ?: uiState.posts.firstOrNull()
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.width(ListPaneWidth).fillMaxHeight()) {
                        filterRow()
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 88.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            feedItems(uiState) { post ->
                                PostRow(
                                    post = post,
                                    isSelected = post.id == selectedPost?.id,
                                    onClick = { selectedPostId = post.id }
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(0.5.dp)
                            .background(BorderDark)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (selectedPost != null) {
                            key(selectedPost.id) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = ReadableWidth)
                                        .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 88.dp)
                                ) {
                                    PostCard(
                                        post = selectedPost,
                                        showFollowButton = uiState.selectedTab != FeedTab.FOLLOWING,
                                        onAction = onPostAction
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                filterRow()

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
                    feedItems(uiState) { post ->
                        PostCard(
                            post = post,
                            showFollowButton = uiState.selectedTab != FeedTab.FOLLOWING,
                            onAction = onPostAction
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

/** Loading, error and empty states; the posts otherwise. */
private fun LazyListScope.feedItems(
    uiState: FeedUiState,
    post: @Composable (PostItem) -> Unit
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
        items(uiState.posts, key = { it.id }) { post(it) }
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
                    .rippleClickable { onTabSelected(tab) }
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