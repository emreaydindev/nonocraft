import SwiftUI

struct CommentRowData: Identifiable {
    let id: String
    let author: String
    let content: String
    let timeAgo: String
    let isLiked: Bool
}

struct PlaylistRowData: Identifiable {
    let id: String
    let name: String
    let postCount: Int
}

/// What the feed-style screens (feed, profile, library detail) share: the three sheets opened from a post card.
struct PostSheetsModel {
    var comments: [CommentRowData]
    var playlists: [PlaylistRowData]
    var isCommentsOpen: Bool
    /// Author of the post whose menu is open, or nil when the menu is closed.
    var menuPostAuthor: String?
    var isPlaylistPickerOpen: Bool

    var closeComments: () -> Void
    var closeMenu: () -> Void
    var closePlaylistPicker: () -> Void
    var onPlay: () -> Void
    var onAddToPlaylist: () -> Void
    var onGoToProfile: () -> Void
    var onDownload: () -> Void
    var onSelectPlaylist: (String) -> Void
    var onCreatePlaylist: (String) -> Void
}

extension View {
    /// Comments, post menu and playlist picker as native sheets with detents. No custom sheet
    /// background, so they get the system Liquid Glass sheet on iOS 26.
    func postSheets(_ model: PostSheetsModel) -> some View {
        self
            .sheet(isPresented: .init(get: { model.isCommentsOpen }, set: { if !$0 { model.closeComments() } })) {
                CommentsSheetContent(comments: model.comments, onClose: model.closeComments)
                    .presentationDetents([.medium, .large])
                    .presentationDragIndicator(.visible)
            }
            .sheet(isPresented: .init(get: { model.menuPostAuthor != nil }, set: { if !$0 { model.closeMenu() } })) {
                PostMenuSheetContent(author: model.menuPostAuthor ?? "", model: model)
                    .presentationDetents([.medium])
                    .presentationDragIndicator(.visible)
            }
            .sheet(isPresented: .init(get: { model.isPlaylistPickerOpen }, set: { if !$0 { model.closePlaylistPicker() } })) {
                PlaylistSelectionSheet(model: model)
                    .presentationDetents([.medium, .large])
            }
    }
}

private struct CommentsSheetContent: View {
    let comments: [CommentRowData]
    let onClose: () -> Void
    @State private var draft = ""

    var body: some View {
        NavigationStack {
            List {
                ForEach(comments) { comment in
                    HStack(alignment: .top, spacing: 12) {
                        Text(comment.author.prefix(1).uppercased())
                            .font(.app(14, .bold))
                            .foregroundStyle(Color.neonCyan)
                            .frame(width: 36, height: 36)
                            .background(Color.surfaceVariantDark, in: Circle())
                            .accessibilityHidden(true)
                        VStack(alignment: .leading, spacing: 4) {
                            HStack(spacing: 8) {
                                Text("@\(comment.author)")
                                    .font(.subheadline.weight(.bold))
                                Text(comment.timeAgo)
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                            Text(comment.content)
                                .font(.body)
                        }
                        Spacer(minLength: 8)
                        Image(systemName: comment.isLiked ? "heart.fill" : "heart")
                            .foregroundStyle(comment.isLiked ? Color.dangerRed : Color.textSecondary)
                            .accessibilityLabel(L10n.string("post_you_liked"))
                            .accessibilityHidden(!comment.isLiked)
                    }
                    // One VoiceOver stop per comment; "Şikayet Et" comes from the swipe action.
                    .accessibilityElement(children: .combine)
                    .listRowBackground(Color.clear)
                    .swipeActions {
                        Button(L10n.string("common_report"), systemImage: "exclamationmark.bubble") {}
                            .tint(.dangerRed)
                    }
                    .contextMenu {
                        Button(role: .destructive) {} label: { Label(L10n.string("common_report"), systemImage: "exclamationmark.bubble") }
                    }
                }
            }
            .listStyle(.plain)
            .scrollContentBackground(.hidden)
            .overlay {
                if comments.isEmpty {
                    ContentUnavailableView(L10n.string("post_no_comments"), systemImage: "bubble.left.and.bubble.right")
                }
            }
            .safeAreaInset(edge: .bottom) { composer }
            .navigationTitle(L10n.string("post_comments_title"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(L10n.string("common_close"), systemImage: "xmark", action: onClose)
                }
            }
        }
    }

    private var composer: some View {
        HStack(spacing: 10) {
            TextField(L10n.string("post_add_comment"), text: $draft, axis: .vertical)
                .lineLimit(1...4)
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
                .appGlass(.regular, in: RoundedRectangle(cornerRadius: 22, style: .continuous))

            // Posting comments is not connected to the API yet (same as Android).
            Button { draft = "" } label: {
                Image(systemName: "arrow.up")
                    .fontWeight(.bold)
                    .foregroundStyle(Color.onNeon)
            }
            .buttonStyle(.appGlassProminent)
            .tint(.neonCyan)
            .disabled(draft.trimmingCharacters(in: .whitespaces).isEmpty)
            .accessibilityLabel(L10n.string("common_send"))
        }
        .tint(.neonCyan)
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
}

private struct PostMenuSheetContent: View {
    let author: String
    let model: PostSheetsModel

    var body: some View {
        List {
            Section {
                row(L10n.string("post_menu_play_now"), systemImage: "play.fill", action: model.onPlay)
                row(L10n.string("post_menu_save"), systemImage: "bookmark", action: model.closeMenu)
                row(L10n.string("post_menu_add_to_playlist"), systemImage: "text.badge.plus", action: model.onAddToPlaylist)
                Button(action: model.onDownload) {
                    HStack {
                        Label(L10n.string("post_menu_download"), systemImage: "arrow.down.to.line")
                        Spacer()
                        ProBadge()
                    }
                }
                row(L10n.format("post_menu_go_to_profile", author), systemImage: "person.fill", action: model.onGoToProfile)
            }
            Section {
                Button(role: .destructive, action: model.closeMenu) {
                    Label(L10n.string("common_report"), systemImage: "exclamationmark.bubble.fill")
                }
                .foregroundStyle(Color.dangerRed)
            }
        }
        .foregroundStyle(Color.textPrimary)
        .scrollContentBackground(.hidden)
        .scrollBounceBehavior(.basedOnSize)
    }

    private func row(_ title: String, systemImage: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Label(title, systemImage: systemImage)
        }
    }
}

private struct PlaylistSelectionSheet: View {
    let model: PostSheetsModel
    @State private var isCreating = false
    @State private var newName = ""

    var body: some View {
        NavigationStack {
            List {
                Section {
                    ForEach(model.playlists) { playlist in
                        Button { model.onSelectPlaylist(playlist.id) } label: {
                            LabeledContent {
                                Text(L10n.format("playlist_puzzle_count", playlist.postCount))
                            } label: {
                                Label(playlist.name, systemImage: "music.note.list")
                            }
                        }
                        .foregroundStyle(Color.textPrimary)
                    }
                }
                Section {
                    Button(L10n.string("playlist_create_new"), systemImage: "plus") {
                        newName = ""
                        isCreating = true
                    }
                    .foregroundStyle(Color.purpleAccent)
                }
            }
            .scrollContentBackground(.hidden)
            .navigationTitle(L10n.string("playlist_add_to_list"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(L10n.string("common_close"), systemImage: "xmark", action: model.closePlaylistPicker)
                }
            }
            .alert(L10n.string("playlist_new"), isPresented: $isCreating) {
                TextField(L10n.string("playlist_name"), text: $newName)
                Button(L10n.string("common_cancel"), role: .cancel) {}
                Button(L10n.string("common_add")) {
                    let name = newName.trimmingCharacters(in: .whitespaces)
                    if !name.isEmpty { model.onCreatePlaylist(name) }
                }
            }
        }
        .tint(.purpleAccent)
    }
}
