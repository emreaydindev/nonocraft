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
        ScreenBackground {
            VStack(alignment: .leading, spacing: 0) {
                Text("Keşfet")
                    .font(.app(28, .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.horizontal, 18)
                    .padding(.vertical, 16)

                filterRow

                ScrollView {
                    LazyVStack(spacing: 24) {
                        if uiState.isLoading {
                            ProgressView().tint(.neonCyan).padding(32)
                        } else if let error = uiState.error {
                            Text(error).font(.app(14)).foregroundColor(.dangerRed).padding(32)
                        } else if uiState.posts.isEmpty {
                            Text("Henüz bir bulmaca bulunamadı.").font(.app(14)).foregroundColor(.textMuted).padding(32)
                        } else {
                            ForEach(uiState.posts, id: \.id) { post in
                                PostCard(post: post, showFollowButton: uiState.selectedTab != .following) { action in
                                    handle(action, for: post)
                                }
                            }
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, 18)
                    .padding(.top, 16)
                    .padding(.bottom, 88)
                }
            }
        }
        .overlay(alignment: .bottomTrailing) {
            Button(action: onNavigateToCrafting) {
                HStack(spacing: 8) {
                    Image(systemName: "paintbrush.fill")
                    Text("Oluştur").font(.app(15, .bold))
                }
                .foregroundColor(.white)
                .padding(.horizontal, 20)
                .frame(height: 56)
                .background(Color.purpleAccent)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .shadow(color: .black.opacity(0.3), radius: 6, y: 3)
            }
            .buttonStyle(.plain)
            .padding(20)
        }
        .confirmationDialog(
            uiState.selectedTab.label,
            isPresented: .init(get: { uiState.showTimeRangeDropdown }, set: { if !$0 { viewModel.dismissTimeRangeDropdown() } }),
            titleVisibility: .visible
        ) {
            ForEach(TimeRange.allCases, id: \.self) { range in
                Button(range == uiState.selectedTimeRange ? "✓ \(range.label)" : range.label) {
                    viewModel.selectTimeRange(range: range)
                }
            }
        }
        .postSheets(sheets)
    }

    private var filterRow: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 10) {
                ForEach(FeedTab.allCases, id: \.self) { tab in
                    let isSelected = tab == uiState.selectedTab
                    let supportsTimeRange = tab == .latest || tab == .mostLiked
                    Button { viewModel.selectTab(tab: tab) } label: {
                        HStack(spacing: 2) {
                            Text(tab.label).font(.app(13, isSelected ? .bold : .medium))
                            if supportsTimeRange {
                                Image(systemName: "arrowtriangle.down.fill").font(.system(size: 7))
                                    .padding(.leading, 4)
                            }
                        }
                        .foregroundColor(isSelected ? .backgroundDark : .textMuted)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(isSelected ? Color.neonCyan : Color.surfaceDark)
                        .clipShape(Capsule())
                        .overlay(Capsule().stroke(isSelected ? Color.neonCyan : Color.borderDark, lineWidth: 1))
                    }
                    .buttonStyle(.plain)
                    .accessibilityAddTraits(isSelected ? .isSelected : [])
                }
            }
            .padding(.horizontal, 18)
            .padding(.vertical, 4)
        }
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
