import Shared
import SwiftUI

struct LibraryDetailScreen: View {
    let categoryId: String
    let onNavigateToGame: (String) -> Void
    let onNavigateToProfile: (String) -> Void

    @StateObject private var model: ObservedViewModel<LibraryDetailViewModel, LibraryDetailUiState>

    init(
        categoryId: String,
        onNavigateToGame: @escaping (String) -> Void,
        onNavigateToProfile: @escaping (String) -> Void
    ) {
        self.categoryId = categoryId
        self.onNavigateToGame = onNavigateToGame
        self.onNavigateToProfile = onNavigateToProfile
        _model = StateObject(wrappedValue: ObservedViewModel(
            { $0.libraryDetailViewModel(categoryId: categoryId) }, state: \.uiState
        ))
    }

    private var uiState: LibraryDetailUiState { model.state }
    private var viewModel: LibraryDetailViewModel { model.viewModel }

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 20) {
                ForEach(uiState.posts, id: \.id) { post in
                    PostCard(post: post, showFollowButton: false) { action in
                        switch action {
                        case .play: onNavigateToGame(post.id)
                        case .profile: onNavigateToProfile(post.author)
                        case .like: viewModel.handlePostAction(action: PostActionLikeClick(postId: post.id))
                        case .comment: viewModel.openComments(postId: post.id)
                        case .more: viewModel.openMenu(postId: post.id)
                        case .follow: viewModel.handlePostAction(action: PostActionFollowClick(postId: post.id))
                        }
                    }
                }
            }
            .padding(.horizontal, 18)
            .padding(.vertical, 16)
            .frame(maxWidth: 640)
            .frame(maxWidth: .infinity)
        }
        .overlay {
            if uiState.isLoading {
                ProgressView().tint(.neonCyan)
            } else if uiState.posts.isEmpty {
                ContentUnavailableView("Burada henüz bir şey yok.", systemImage: "tray")
            }
        }
        .background { ScreenBackground { EmptyView() } }
        .navigationTitle(uiState.title)
        .navigationBarTitleDisplayMode(.inline)
        .postSheets(sheets)
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
