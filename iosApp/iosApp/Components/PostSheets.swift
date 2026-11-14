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
    func postSheets(_ model: PostSheetsModel) -> some View {
        self
            .sheet(isPresented: .init(get: { model.isCommentsOpen }, set: { if !$0 { model.closeComments() } })) {
                CommentsSheetContent(comments: model.comments)
                    .presentationDetents([.medium, .large])
                    .presentationBackground(Color.backgroundDark)
                    .presentationDragIndicator(.visible)
            }
            .sheet(isPresented: .init(get: { model.menuPostAuthor != nil }, set: { if !$0 { model.closeMenu() } })) {
                PostMenuSheetContent(author: model.menuPostAuthor ?? "", model: model)
                    .presentationDetents([.height(400)])
                    .presentationBackground(Color.backgroundDark)
                    .presentationDragIndicator(.visible)
            }
            .sheet(isPresented: .init(get: { model.isPlaylistPickerOpen }, set: { if !$0 { model.closePlaylistPicker() } })) {
                PlaylistSelectionSheet(model: model)
                    .presentationDetents([.medium])
                    .presentationBackground(Color.surfaceDark)
            }
    }
}

private struct CommentsSheetContent: View {
    let comments: [CommentRowData]
    @State private var draft = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Yorumlar")
                .font(.app(20, .bold))
                .foregroundColor(.textPrimary)
                .padding(.horizontal, 24)
                .padding(.top, 28)
                .padding(.bottom, 16)

            ScrollView {
                VStack(spacing: 20) {
                    ForEach(comments) { comment in
                        HStack(alignment: .top, spacing: 12) {
                            Text(comment.author.prefix(1).uppercased())
                                .font(.app(14, .bold))
                                .foregroundColor(.neonCyan)
                                .frame(width: 36, height: 36)
                                .background(Color.surfaceVariantDark)
                                .clipShape(Circle())
                            VStack(alignment: .leading, spacing: 4) {
                                HStack(spacing: 8) {
                                    Text("@\(comment.author)")
                                        .font(.app(14, .bold))
                                        .foregroundColor(.textPrimary)
                                    Text(comment.timeAgo)
                                        .font(.app(12))
                                        .foregroundColor(.textMuted)
                                }
                                Text(comment.content)
                                    .font(.app(15))
                                    .foregroundColor(.textPrimary)
                            }
                            Spacer(minLength: 8)
                            Image(systemName: comment.isLiked ? "heart.fill" : "heart")
                                .font(.system(size: 16))
                                .foregroundColor(comment.isLiked ? .dangerRed : .textMuted)
                        }
                        .contextMenu {
                            Button(role: .destructive) {} label: { Label("Şikayet Et", systemImage: "exclamationmark.bubble") }
                        }
                    }
                }
                .padding(.horizontal, 24)
            }

            HStack(spacing: 12) {
                TextField("", text: $draft, prompt: Text("Yorum ekle...").font(.app(14)).foregroundColor(.textMuted))
                    .font(.app(14))
                    .foregroundColor(.textPrimary)
                    .tint(.neonCyan)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                    .background(Color.surfaceDark)
                    .clipShape(Capsule())
                    .overlay(Capsule().stroke(Color.borderDark, lineWidth: 1))

                // Posting comments is not connected to the API yet (same as Android).
                Button { draft = "" } label: {
                    Image(systemName: "paperplane.fill")
                        .font(.system(size: 16))
                        .foregroundColor(draft.isEmpty ? .textMuted : .backgroundDark)
                        .frame(width: 44, height: 44)
                        .background(draft.isEmpty ? Color.surfaceVariantDark : Color.neonCyan)
                        .clipShape(Circle())
                }
                .buttonStyle(.plain)
                .disabled(draft.trimmingCharacters(in: .whitespaces).isEmpty)
                .accessibilityLabel("Gönder")
            }
            .padding(.horizontal, 24)
            .padding(.vertical, 12)
        }
    }
}

private struct PostMenuSheetContent: View {
    let author: String
    let model: PostSheetsModel

    var body: some View {
        VStack(spacing: 0) {
            MenuRowItem(icon: "play.fill", text: "Şimdi Oyna", action: model.onPlay)
            MenuRowItem(icon: "bookmark", text: "Kaydet", action: model.closeMenu)
            MenuRowItem(icon: "text.badge.plus", text: "Oynatma Listesine Ekle", action: model.onAddToPlaylist)
            MenuRowItem(icon: "arrow.down.to.line", text: "İndir", trailingIcon: "rosette", action: model.onDownload)
            MenuRowItem(icon: "person.fill", text: "@\(author) Profiline Git", action: model.onGoToProfile)
            MenuRowItem(icon: "exclamationmark.bubble.fill", text: "Şikayet Et", tint: .dangerRed, action: model.closeMenu)
            Spacer(minLength: 0)
        }
        .padding(.top, 28)
    }
}

private struct MenuRowItem: View {
    let icon: String
    let text: String
    var tint: Color = .textPrimary
    var trailingIcon: String?
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                Image(systemName: icon)
                    .font(.system(size: 19))
                    .frame(width: 24, height: 24)
                Text(text)
                    .font(.app(16, .medium))
                Spacer()
                if let trailingIcon {
                    Image(systemName: trailingIcon)
                        .font(.system(size: 19))
                        .foregroundColor(.gameGold)
                }
            }
            .foregroundColor(tint)
            .padding(.horizontal, 24)
            .padding(.vertical, 16)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private struct PlaylistSelectionSheet: View {
    let model: PostSheetsModel
    @State private var isCreating = false
    @State private var newName = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack {
                Text("Listeye Ekle")
                    .font(.app(18, .bold))
                    .foregroundColor(.textPrimary)
                Spacer()
                Button(action: model.closePlaylistPicker) {
                    Image(systemName: "xmark")
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundColor(.textMuted)
                        .frame(width: 32, height: 32)
                }
                .accessibilityLabel("Kapat")
            }
            .padding(.bottom, 12)

            ScrollView {
                VStack(spacing: 0) {
                    ForEach(model.playlists) { playlist in
                        Button { model.onSelectPlaylist(playlist.id) } label: {
                            HStack(spacing: 14) {
                                Image(systemName: "bookmark.fill")
                                    .foregroundColor(.purpleAccent)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(playlist.name)
                                        .font(.app(15, .medium))
                                        .foregroundColor(.textPrimary)
                                    Text("\(playlist.postCount) bulmaca")
                                        .font(.app(12))
                                        .foregroundColor(.textMuted)
                                }
                                Spacer()
                            }
                            .padding(.vertical, 12)
                            .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    }

                    if isCreating {
                        HStack(spacing: 10) {
                            TextField("", text: $newName, prompt: Text("Liste adı...").font(.app(13)).foregroundColor(.textMuted))
                                .font(.app(14))
                                .foregroundColor(.textPrimary)
                                .tint(.neonCyan)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 10)
                                .background(Color.surfaceVariantDark)
                                .clipShape(RoundedRectangle(cornerRadius: 10))
                            Button("Ekle") {
                                let name = newName.trimmingCharacters(in: .whitespaces)
                                if !name.isEmpty { model.onCreatePlaylist(name) }
                            }
                            .font(.app(13, .bold))
                            .foregroundColor(.purpleAccent)
                        }
                        .padding(.vertical, 12)
                    } else {
                        Button { isCreating = true } label: {
                            HStack(spacing: 14) {
                                Image(systemName: "plus").foregroundColor(.purpleAccent)
                                Text("Yeni Liste Oluştur")
                                    .font(.app(15, .medium))
                                    .foregroundColor(.purpleAccent)
                                Spacer()
                            }
                            .padding(.vertical, 12)
                            .contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
        }
        .padding(24)
    }
}
