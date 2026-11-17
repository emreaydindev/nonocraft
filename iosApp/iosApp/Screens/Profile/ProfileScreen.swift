import Shared
import SwiftUI

struct ProfileScreen: View {
    let userId: String
    let onNavigateToGame: (String) -> Void

    @StateObject private var model = ObservedViewModel({ $0.profileViewModel() }, state: \.uiState)

    private var uiState: FeedUiState { model.state }
    private var viewModel: ProfileViewModel { model.viewModel }

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 20) {
                header
                ForEach(uiState.posts, id: \.id) { post in
                    PostCard(post: post, showFollowButton: false) { action in
                        switch action {
                        case .play: onNavigateToGame(post.id)
                        case .like: viewModel.handlePostAction(action: PostActionLikeClick(postId: post.id))
                        case .comment: viewModel.openComments(postId: post.id)
                        case .more: viewModel.openMenu(postId: post.id)
                        case .follow: viewModel.handlePostAction(action: PostActionFollowClick(postId: post.id))
                        case .profile: break
                        }
                    }
                }
            }
            .padding(.horizontal, 18)
            .padding(.vertical, 16)
            .frame(maxWidth: 640)
            .frame(maxWidth: .infinity)
        }
        .background { ScreenBackground { EmptyView() } }
        .navigationTitle("@\(userId)")
        .navigationBarTitleDisplayMode(.inline)
        .task { viewModel.loadUserPosts(userId: userId) }
        .postSheets(sheets)
    }

    private var header: some View {
        DashboardCard(cornerRadius: 24) {
            VStack(spacing: 0) {
                Text(userId.prefix(1).uppercased())
                    .font(.app(32, .bold))
                    .foregroundColor(.neonCyan)
                    .frame(width: 80, height: 80)
                    .background(Color.surfaceVariantDark)
                    .clipShape(Circle())
                    .overlay(Circle().stroke(Color.neonCyan.opacity(0.5), lineWidth: 2))
                Text("@\(userId)")
                    .font(.app(20, .bold))
                    .foregroundColor(.textPrimary)
                    .padding(.top, 12)
                Text("Nonocraft Bulmaca Meraklısı")
                    .font(.subheadline)
                    .foregroundColor(.textMuted)
                    .padding(.top, 4)
                // Demo numbers, same as Android, until user stats come from the API.
                HStack {
                    stat("28", "Bulmaca")
                    stat("156", "Beğeni")
                    stat("12", "Liste")
                }
                .padding(.top, 20)
            }
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 24)
            .padding(.vertical, 28)
        }
    }

    private func stat(_ value: String, _ label: String) -> some View {
        VStack(spacing: 0) {
            Text(value).font(.app(22, .bold)).foregroundColor(.textPrimary)
            Text(label).font(.footnote).foregroundStyle(Color.textSecondary)
        }
        .frame(maxWidth: .infinity)
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
            onGoToProfile: { viewModel.closeMenu() },
            onDownload: { if let menuPost { viewModel.downloadPuzzle(postId: menuPost.id) } },
            onSelectPlaylist: { viewModel.addToPlaylist(playlistId: $0) },
            onCreatePlaylist: { viewModel.createAndAddToPlaylist(name: $0) }
        )
    }
}
