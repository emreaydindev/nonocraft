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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.core.os.bundleOf
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neilb.nonocraft.presentation.adaptive.ListPaneWidth
import com.neilb.nonocraft.presentation.adaptive.isWideWindow
import com.neilb.nonocraft.presentation.screens.dashboard.library.detail.LibraryDetailScreen
import com.neilb.nonocraft.presentation.screens.dashboard.library.detail.LibraryDetailViewModel
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.SurfaceDark
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

/** One Library section: a card in the phone grid, a row in the list pane of the two-pane layout. */
private data class LibrarySection(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color,
    val isPremium: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = koinViewModel(),
    onNavigateToCrafting: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToCategory: (categoryId: String) -> Unit,
    onNavigateToPlaylist: (playlistId: String) -> Unit,
    onNavigateToGame: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    // Tablets, unfolded foldables and wide windows show the picked section next to the list instead of
    // opening its own page.
    val isTwoPane = isWideWindow()
    val isTwoPaneState by rememberUpdatedState(isTwoPane)
    var selectedId by rememberSaveable { mutableStateOf("liked") }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LibraryUiEvent.NavigateToCategory ->
                    if (isTwoPaneState) selectedId = event.categoryId else onNavigateToCategory(event.categoryId)
                is LibraryUiEvent.NavigateToPlaylist ->
                    if (isTwoPaneState) selectedId = event.playlistId else onNavigateToPlaylist(event.playlistId)
                is LibraryUiEvent.ShowCreatePlaylistDialog -> showCreatePlaylistDialog = true
            }
        }
    }

    val sections = listOf(
        LibrarySection("liked", "Beğenilenler", "${uiState.likedArtsCount} Pixelart", Icons.Rounded.Favorite, DangerRed) {
            viewModel.handleAction(LibraryAction.OnCategoryClick("liked"))
        },
        LibrarySection(
            "downloaded", "İndirilenler", "${uiState.downloadedLevelsCount} Bölüm", Icons.Rounded.Download, GameGold,
            isPremium = true
        ) {
            viewModel.handleAction(LibraryAction.OnCategoryClick("downloaded"))
            onNavigateToPremium()
        },
        LibrarySection("shared", "Paylaşılanlar", "${uiState.sharedArtsCount} Tasarım", Icons.Rounded.Palette, NeonCyan) {
            viewModel.handleAction(LibraryAction.OnCategoryClick("shared"))
        },
        LibrarySection("drafts", "Taslaklar", "${uiState.draftArtsCount} Kayıt", Icons.Rounded.Edit, TextMuted) {
            viewModel.handleAction(LibraryAction.OnCategoryClick("drafts"))
            onNavigateToCrafting()
        },
        LibrarySection(
            "completed", "Çözülenler", "${uiState.completedLevelsCount} Bölüm", Icons.Rounded.CheckCircle, Color(0xFF4CAF50)
        ) {
            viewModel.handleAction(LibraryAction.OnCategoryClick("completed"))
        },
        LibrarySection(
            "dailies", "Günlük Serüven", "${uiState.completedDailiesCount} Çözüm", Icons.Rounded.Today, Color(0xFFB388FF)
        ) {
            viewModel.handleAction(LibraryAction.OnCategoryClick("dailies"))
        },
    )
    val onCreatePlaylist = { viewModel.handleAction(LibraryAction.OnCreatePlaylist) }
    val onPlaylistClick = { id: String -> viewModel.handleAction(LibraryAction.OnPlaylistClick(id)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        GridBackground()

        if (isTwoPane) {
            Row(modifier = Modifier.fillMaxSize()) {
                LibraryRowList(
                    sections = sections,
                    playlists = uiState.playlists,
                    selectedId = selectedId,
                    onCreatePlaylist = onCreatePlaylist,
                    onPlaylistClick = onPlaylistClick,
                    modifier = Modifier.width(ListPaneWidth).fillMaxHeight()
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(0.5.dp)
                        .background(BorderDark)
                )
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // A fresh view model per section, fed the id the way the navigation route would.
                    key(selectedId) {
                        LibraryDetailScreen(
                            viewModel = libraryDetailViewModel(selectedId),
                            onNavigateBack = null,
                            onNavigateToGame = onNavigateToGame,
                            onNavigateToProfile = onNavigateToProfile
                        )
                    }
                }
            }
        } else if (LocalDensity.current.fontScale >= LargeFontScale) {
            // The square cards can't hold their text at the largest font scales; the rows can.
            LibraryRowList(
                sections = sections,
                playlists = uiState.playlists,
                selectedId = null,
                onCreatePlaylist = onCreatePlaylist,
                onPlaylistClick = onPlaylistClick,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LibraryCardGrid(
                sections = sections,
                playlists = uiState.playlists,
                onCreatePlaylist = onCreatePlaylist,
                onPlaylistClick = onPlaylistClick
            )
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

/** Font scale from which card grids switch to rows; 1.5 is the second-largest system setting. */
private const val LargeFontScale = 1.5f

/** [LibraryDetailViewModel] for an embedded section, with the id where the route would put it. */
@Composable
private fun libraryDetailViewModel(categoryId: String): LibraryDetailViewModel {
    val owner = checkNotNull(LocalViewModelStoreOwner.current)
    val defaults = (owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelCreationExtras
        ?: CreationExtras.Empty
    return viewModel(
        viewModelStoreOwner = owner,
        key = "library-detail-$categoryId",
        extras = MutableCreationExtras(defaults).apply {
            set(DEFAULT_ARGS_KEY, bundleOf("categoryId" to categoryId))
        }
    )
}

/** Phones: section cards and playlists in two-column grids; each opens its own page. */
@Composable
private fun LibraryCardGrid(
    sections: List<LibrarySection>,
    playlists: List<PlaylistItem>,
    onCreatePlaylist: () -> Unit,
    onPlaylistClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            LibraryTitle()
        }

        items(sections, key = { it.id }) { section ->
            LibraryGridCard(
                title = section.title,
                subtitle = section.subtitle,
                icon = section.icon,
                iconTint = section.tint,
                isPremium = section.isPremium,
                onClick = section.onClick
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            PlaylistsTitle(modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
        }

        item {
            DashboardCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .rippleClickable(onCreatePlaylist),
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

        items(playlists, key = { it.id }) { playlist ->
            PlaylistGridCard(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist.id) }
            )
        }
    }
}

/**
 * Sections and playlists as rows: the list pane of the two-pane layout ([selectedId] highlighted), and
 * the phone layout at the largest font scales ([selectedId] null).
 */
@Composable
private fun LibraryRowList(
    sections: List<LibrarySection>,
    playlists: List<PlaylistItem>,
    selectedId: String?,
    onCreatePlaylist: () -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { LibraryTitle() }
        items(sections, key = { it.id }) { section ->
            LibraryRow(
                title = section.title,
                subtitle = section.subtitle,
                icon = section.icon,
                tint = section.tint,
                isPremium = section.isPremium,
                isSelected = section.id == selectedId,
                onClick = section.onClick
            )
        }
        item { PlaylistsTitle(modifier = Modifier.padding(top = 20.dp)) }
        item {
            LibraryRow(
                title = "Yeni Liste",
                subtitle = null,
                icon = Icons.Rounded.Add,
                tint = TextPrimary,
                isSelected = false,
                onClick = onCreatePlaylist
            )
        }
        items(playlists, key = { it.id }) { playlist ->
            LibraryRow(
                title = playlist.name,
                subtitle = "${playlist.postCount} Bulmaca",
                icon = Icons.Rounded.QueueMusic,
                tint = Color(playlist.coverColor),
                isSelected = playlist.id == selectedId,
                onClick = { onPlaylistClick(playlist.id) }
            )
        }
    }
}

@Composable
private fun LibraryTitle() {
    Text(
        text = "Kütüphanem",
        color = TextPrimary,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = AppFont,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun PlaylistsTitle(modifier: Modifier = Modifier) {
    Text(
        text = "Oynatma Listeleri",
        color = TextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = AppFont,
        modifier = modifier
    )
}

@Composable
private fun LibraryRow(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    tint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    isPremium: Boolean = false
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isSelected) SurfaceVariantDark else SurfaceDark)
            .border(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) NeonCyan else BorderDark, shape)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontFamily = AppFont
                )
            }
        }
        if (isPremium) {
            ProBadge()
        }
    }
}

@Composable
private fun ProBadge() {
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
                    ProBadge()
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