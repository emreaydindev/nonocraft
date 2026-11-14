package com.neilb.nonocraft.presentation.screens.dashboard.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.neilb.nonocraft.presentation.components.PlaylistSelectionDialog
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardCard
import com.neilb.nonocraft.presentation.screens.dashboard.components.rippleClickable
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.GameGold
import com.neilb.nonocraft.presentation.theme.GridBackground
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = koinViewModel(),
    onNavigateToCrafting: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToCategory: (categoryId: String) -> Unit,
    onNavigateToPlaylist: (playlistId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LibraryUiEvent.NavigateToCategory -> onNavigateToCategory(event.categoryId)
                is LibraryUiEvent.NavigateToPlaylist -> onNavigateToPlaylist(event.playlistId)
                is LibraryUiEvent.ShowCreatePlaylistDialog -> showCreatePlaylistDialog = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        GridBackground()

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Kütüphanem",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                LibraryGridCard(
                    title = "Beğenilenler",
                    subtitle = "${uiState.likedArtsCount} Pixelart",
                    icon = Icons.Rounded.Favorite,
                    iconTint = DangerRed,
                    onClick = { viewModel.handleAction(LibraryAction.OnCategoryClick("liked")) }
                )
            }
            item {
                LibraryGridCard(
                    title = "İndirilenler",
                    subtitle = "${uiState.downloadedLevelsCount} Bölüm",
                    icon = Icons.Rounded.Download,
                    iconTint = GameGold,
                    isPremium = true,
                    onClick = {
                        viewModel.handleAction(LibraryAction.OnCategoryClick("downloaded"))
                        onNavigateToPremium()
                    }
                )
            }
            item {
                LibraryGridCard(
                    title = "Paylaşılanlar",
                    subtitle = "${uiState.sharedArtsCount} Tasarım",
                    icon = Icons.Rounded.Palette,
                    iconTint = NeonCyan,
                    onClick = { viewModel.handleAction(LibraryAction.OnCategoryClick("shared")) }
                )
            }
            item {
                LibraryGridCard(
                    title = "Taslaklar",
                    subtitle = "${uiState.draftArtsCount} Kayıt",
                    icon = Icons.Rounded.Edit,
                    iconTint = TextMuted,
                    onClick = {
                        viewModel.handleAction(LibraryAction.OnCategoryClick("drafts"))
                        onNavigateToCrafting()
                    }
                )
            }
            item {
                LibraryGridCard(
                    title = "Çözülenler",
                    subtitle = "${uiState.completedLevelsCount} Bölüm",
                    icon = Icons.Rounded.CheckCircle,
                    iconTint = Color(0xFF4CAF50),
                    onClick = { viewModel.handleAction(LibraryAction.OnCategoryClick("completed")) }
                )
            }
            item {
                LibraryGridCard(
                    title = "Günlük Serüven",
                    subtitle = "${uiState.completedDailiesCount} Çözüm",
                    icon = Icons.Rounded.Today,
                    iconTint = Color(0xFFB388FF),
                    onClick = { viewModel.handleAction(LibraryAction.OnCategoryClick("dailies")) }
                )
            }

            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "Oynatma Listeleri",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
            }

            item {
                DashboardCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f)
                        .rippleClickable { viewModel.handleAction(LibraryAction.OnCreatePlaylist) },
                    cornerRadius = 24
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Text(
                            text = "Yeni Liste",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFont,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }

            items(uiState.playlists) { playlist ->
                PlaylistGridCard(
                    playlist = playlist,
                    onClick = { viewModel.handleAction(LibraryAction.OnPlaylistClick(playlist.id)) }
                )
            }
        }
    }

    if (showCreatePlaylistDialog) {
        PlaylistSelectionDialog(
            playlists = emptyList(),
            onDismiss = { showCreatePlaylistDialog = false },
            onSelectPlaylist = {},
            onCreatePlaylist = { playlistName ->
                viewModel.handleAction(LibraryAction.OnCreatePlaylistSubmit(playlistName))
                showCreatePlaylistDialog = false
            }
        )
    }
}

@Composable
private fun LibraryGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isPremium: Boolean = false,
    onClick: () -> Unit
) {
    DashboardCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .rippleClickable(onClick),
        cornerRadius = 24
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (isPremium) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GameGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PRO",
                            color = GameGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppFont
                        )
                    }
                }
            }

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PlaylistGridCard(
    playlist: PlaylistItem,
    onClick: () -> Unit
) {
    DashboardCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .rippleClickable(onClick),
        cornerRadius = 24
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(playlist.coverColor).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.QueueMusic,
                    contentDescription = null,
                    tint = Color(playlist.coverColor),
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = playlist.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont,
                    maxLines = 1
                )
                Text(
                    text = "${playlist.postCount} Bulmaca",
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontFamily = AppFont,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}