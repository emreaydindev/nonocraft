import Shared
import SwiftUI

struct FeedScreen: View {
    let onNavigateToGame: (String) -> Void
    let onNavigateToProfile: (String) -> Void
    let onNavigateToCrafting: () -> Void

    @StateObject private var model = ObservedViewModel({ $0.feedViewModel() }, state: \.uiState)

    private var uiState: FeedUiState { model.state }
    private var viewModel: FeedViewModel { model.viewModel }

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 20) {
                filterRow
                    .padding(.horizontal, -18)

                if uiState.isLoading {
                    ProgressView().tint(.neonCyan).padding(32)
                } else if let error = uiState.error {
                    ContentUnavailableView(error, systemImage: "wifi.exclamationmark")
                        .foregroundStyle(Color.dangerRed)
                } else if uiState.posts.isEmpty {
                    ContentUnavailableView("Henüz bir bulmaca bulunamadı.", systemImage: "square.grid.3x3")
                } else {
                    ForEach(uiState.posts, id: \.id) { post in
                        PostCard(post: post, showFollowButton: uiState.selectedTab != .following) { action in
                            handle(action, for: post)
                        }
                    }
                }
            }
            .padding(.horizontal, 18)
            .padding(.bottom, 24)
            .frame(maxWidth: 640)
            .frame(maxWidth: .infinity)
        }
        .background { ScreenBackground { EmptyView() } }
        .navigationTitle("Keşfet")
        // Large title on the same row as the toolbar button instead of a row below it.
        .toolbarTitleDisplayMode(.inlineLarge)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Oluştur", systemImage: "paintbrush.pointed.fill", action: onNavigateToCrafting)
            }
        }
        .postSheets(sheets)
    }

    /// Feed tabs as glass chips. The selected Newest / Most liked chip opens a menu with the time range.
    private var filterRow: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 10) {
                ForEach(FeedTab.allCases, id: \.self) { tab in
                    let isSelected = tab == uiState.selectedTab
                    let supportsTimeRange = tab == .latest || tab == .mostLiked
                    if isSelected && supportsTimeRange {
                        Menu {
                            Picker("Zaman aralığı", selection: timeRange) {
                                ForEach(TimeRange.allCases, id: \.self) { Text($0.label).tag($0) }
                            }
                        } label: {
                            // Menus ignore custom button styles, so the selected look is drawn here:
                            // same capsule and size as the prominent glass chip.
                            chipLabel(tab.label, showsMenuIndicator: true, isSelected: true)
                                .padding(.horizontal, 16)
                                .padding(.vertical, 6)
                                .background(Color.neonCyan, in: Capsule())
                                .contentShape(Capsule())
                        }
                        .accessibilityAddTraits(.isSelected)
                    } else {
                        Button { viewModel.selectTab(tab: tab) } label: {
                            chipLabel(tab.label, showsMenuIndicator: supportsTimeRange, isSelected: isSelected)
                        }
                        .buttonStyle(isSelected ? AppGlassButtonStyle.appGlassProminent : .appGlass)
                        .tint(.neonCyan)
                        .accessibilityAddTraits(isSelected ? .isSelected : [])
                    }
                }
            }
            .padding(.horizontal, 18)
            .padding(.vertical, 4)
        }
        .scrollClipDisabled()
    }

    private func chipLabel(_ title: String, showsMenuIndicator: Bool, isSelected: Bool) -> some View {
        HStack(spacing: 6) {
            Text(title)
            if showsMenuIndicator {
                Image(systemName: "chevron.down")
                    .font(.caption2.weight(.bold))
            }
        }
        .font(.subheadline.weight(isSelected ? .bold : .medium))
        .foregroundStyle(isSelected ? Color.onNeon : Color.textPrimary)
    }

    private var timeRange: Binding<TimeRange> {
        Binding(get: { uiState.selectedTimeRange }, set: { viewModel.selectTimeRange(range: $0) })
    }

    private func handle(_ action: PostCardAction, for post: PostItem) {
        switch action {
        case .play: onNavigateToGame(post.id)
        case .profile: onNavigateToProfile(post.author)
        case .like: viewModel.toggleLike(postId: post.id)
        case .comment: viewModel.openComments(postId: post.id)
        case .more: viewModel.openMenu(postId: post.id)
        case .follow: viewModel.toggleFollow(postId: post.id)
        }
    }

    private var sheets: PostSheetsModel {
        let menuPost = uiState.posts.first { $0.id == uiState.activeMenuPostId }
        return PostSheetsModel(
            comments: uiState.commentsForActivePost.map {
                CommentRowData(id: $0.id, author: $0.author, content: $0.content, timeAgo: $0.timeAgo, isLiked: $0.isLiked)
            },
            playlists: uiState.playlists.map { PlaylistRowData(id: $0.id, name: $0.name, postCount: Int($0.postCount)) },
            isCommentsOpen: uiState.activeCommentPostId != nil,
            menuPostAuthor: menuPost?.author,
            isPlaylistPickerOpen: uiState.activePlaylistPostId != nil,
            closeComments: { viewModel.closeComments() },
            closeMenu: { viewModel.closeMenu() },
            closePlaylistPicker: { viewModel.closePlaylistDialog() },
            onPlay: {
                viewModel.closeMenu()
                if let menuPost { onNavigateToGame(menuPost.id) }
            },
            onAddToPlaylist: { viewModel.openPlaylistDialog() },
            onGoToProfile: {
                viewModel.closeMenu()
                if let menuPost { onNavigateToProfile(menuPost.author) }
            },
            onDownload: { if let menuPost { viewModel.downloadPuzzle(postId: menuPost.id) } },
            onSelectPlaylist: { viewModel.addToPlaylist(playlistId: $0) },
            onCreatePlaylist: { viewModel.createAndAddToPlaylist(name: $0) }
        )
    }
}
