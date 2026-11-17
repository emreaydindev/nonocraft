import Shared
import SwiftUI

/// A library section or playlist as its own page (phones, narrow windows).
struct LibraryDetailScreen: View {
    let categoryId: String
    let onNavigateToGame: (String) -> Void
    let onNavigateToProfile: (String) -> Void

    var body: some View {
        LibraryDetailContent(
            categoryId: categoryId,
            showsTitleHeader: false,
            onNavigateToGame: onNavigateToGame,
            onNavigateToProfile: onNavigateToProfile
        )
        .background { ScreenBackground { EmptyView() } }
    }
}

/// Posts of one library section or playlist. Used as a page, and as the content pane of the Library's
/// two-pane layout, where it shows its title as a heading instead of in the navigation bar.
struct LibraryDetailContent: View {
    let categoryId: String
    let showsTitleHeader: Bool
    let onNavigateToGame: (String) -> Void
    let onNavigateToProfile: (String) -> Void

    @StateObject private var model: ObservedViewModel<LibraryDetailViewModel, LibraryDetailUiState>

    init(
        categoryId: String,
        showsTitleHeader: Bool,
        onNavigateToGame: @escaping (String) -> Void,
        onNavigateToProfile: @escaping (String) -> Void
    ) {
        self.categoryId = categoryId
        self.showsTitleHeader = showsTitleHeader
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
                if showsTitleHeader {
                    Text(uiState.title)
                        .font(.app(22, .bold))
                        .foregroundStyle(Color.textPrimary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .accessibilityAddTraits(.isHeader)
                }
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
        .modifier(InlineNavigationTitle(title: uiState.title, isEnabled: !showsTitleHeader))
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

/// Sets the inline navigation title only when the content is a page of its own; as a pane it must not
/// replace the title of the screen that hosts it.
private struct InlineNavigationTitle: ViewModifier {
    let title: String
    let isEnabled: Bool

    func body(content: Content) -> some View {
        if isEnabled {
            content
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.inline)
        } else {
            content
        }
    }
}
