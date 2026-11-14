import Shared
import SwiftUI

struct LibraryScreen: View {
    let onNavigateToCrafting: () -> Void
    let onNavigateToPremium: () -> Void
    let onNavigateToCategory: (String) -> Void
    let onNavigateToPlaylist: (String) -> Void

    @StateObject private var model = ObservedViewModel({ $0.libraryViewModel() }, state: \.uiState)
    @State private var showCreatePlaylist = false
    @State private var newPlaylistName = ""

    private var uiState: LibraryUiState { model.state }
    private let columns = [GridItem(.flexible(), spacing: 16), GridItem(.flexible(), spacing: 16)]

    var body: some View {
        ScreenBackground {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("Kütüphanem")
                        .font(.app(28, .bold))
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 8)

                    LazyVGrid(columns: columns, spacing: 16) {
                        LibraryGridCard(
                            title: "Beğenilenler", subtitle: "\(uiState.likedArtsCount) Pixelart",
                            icon: "heart.fill", tint: .dangerRed
                        ) { send(LibraryActionOnCategoryClick(categoryId: "liked")) }

                        LibraryGridCard(
                            title: "İndirilenler", subtitle: "\(uiState.downloadedLevelsCount) Bölüm",
                            icon: "arrow.down.to.line", tint: .gameGold, isPremium: true
                        ) {
                            send(LibraryActionOnCategoryClick(categoryId: "downloaded"))
                            onNavigateToPremium()
                        }

                        LibraryGridCard(
                            title: "Paylaşılanlar", subtitle: "\(uiState.sharedArtsCount) Tasarım",
                            icon: "paintpalette.fill", tint: .neonCyan
                        ) { send(LibraryActionOnCategoryClick(categoryId: "shared")) }

                        LibraryGridCard(
                            title: "Taslaklar", subtitle: "\(uiState.draftArtsCount) Kayıt",
                            icon: "pencil", tint: .textMuted
                        ) {
                            send(LibraryActionOnCategoryClick(categoryId: "drafts"))
                            onNavigateToCrafting()
                        }

                        LibraryGridCard(
                            title: "Çözülenler", subtitle: "\(uiState.completedLevelsCount) Bölüm",
                            icon: "checkmark.circle.fill", tint: Color(hex: 0x4CAF50)
                        ) { send(LibraryActionOnCategoryClick(categoryId: "completed")) }

                        LibraryGridCard(
                            title: "Günlük Serüven", subtitle: "\(uiState.completedDailiesCount) Çözüm",
                            icon: "calendar", tint: Color(hex: 0xB388FF)
                        ) { send(LibraryActionOnCategoryClick(categoryId: "dailies")) }
                    }

                    Text("Oynatma Listeleri")
                        .font(.app(20, .bold))
                        .foregroundColor(.textPrimary)
                        .padding(.top, 24)
                        .padding(.bottom, 8)

                    LazyVGrid(columns: columns, spacing: 16) {
                        NewPlaylistCard { send(LibraryActionOnCreatePlaylist.shared) }
                        ForEach(uiState.playlists, id: \.id) { playlist in
                            PlaylistGridCard(playlist: playlist) {
                                send(LibraryActionOnPlaylistClick(playlistId: playlist.id))
                            }
                        }
                    }
                }
                .padding(.horizontal, 18)
                .padding(.top, 24)
                .padding(.bottom, 40)
            }
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .navigateToCategory(let event): onNavigateToCategory(event.categoryId)
                case .navigateToPlaylist(let event): onNavigateToPlaylist(event.playlistId)
                case .showCreatePlaylistDialog:
                    newPlaylistName = ""
                    showCreatePlaylist = true
                }
            }
        }
        .alert("Yeni Liste", isPresented: $showCreatePlaylist) {
            TextField("Liste adı", text: $newPlaylistName)
            Button("İptal", role: .cancel) {}
            Button("Oluştur") {
                let name = newPlaylistName.trimmingCharacters(in: .whitespaces)
                if !name.isEmpty { send(LibraryActionOnCreatePlaylistSubmit(name: name)) }
            }
        }
    }

    private func send(_ action: LibraryAction) {
        model.viewModel.handleAction(action: action)
    }
}

private struct LibraryCardFrame<Content: View>: View {
    let action: () -> Void
    @ViewBuilder var content: Content

    var body: some View {
        Button(action: action) {
            DashboardCard(cornerRadius: 24) {
                content
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .aspectRatio(1.1, contentMode: .fit)
            }
        }
        .buttonStyle(.plain)
    }
}

private struct LibraryGridCard: View {
    let title: String
    let subtitle: String
    let icon: String
    let tint: Color
    var isPremium = false
    let action: () -> Void

    var body: some View {
        LibraryCardFrame(action: action) {
            VStack(alignment: .leading, spacing: 0) {
                HStack(alignment: .top) {
                    Image(systemName: icon)
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundColor(tint)
                        .frame(width: 48, height: 48)
                        .background(tint.opacity(0.15))
                        .clipShape(Circle())
                    Spacer()
                    if isPremium {
                        Text("PRO")
                            .font(.app(10, .bold))
                            .foregroundColor(.gameGold)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.gameGold.opacity(0.2))
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                    }
                }
                Spacer(minLength: 8)
                Text(title)
                    .font(.app(16, .bold))
                    .foregroundColor(.textPrimary)
                    .multilineTextAlignment(.leading)
                Text(subtitle)
                    .font(.app(13))
                    .foregroundColor(.textMuted)
                    .padding(.top, 4)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(20)
        }
    }
}

private struct NewPlaylistCard: View {
    let action: () -> Void

    var body: some View {
        LibraryCardFrame(action: action) {
            VStack(spacing: 16) {
                Image(systemName: "plus")
                    .font(.system(size: 24, weight: .medium))
                    .foregroundColor(.textPrimary)
                    .frame(width: 56, height: 56)
                    .background(Color.surfaceVariantDark)
                    .clipShape(Circle())
                Text("Yeni Liste")
                    .font(.app(15, .bold))
                    .foregroundColor(.textPrimary)
            }
            .padding(16)
        }
    }
}

private struct PlaylistGridCard: View {
    let playlist: PlaylistItem
    let action: () -> Void

    var body: some View {
        let tint = Color(argb: playlist.coverColor)
        LibraryCardFrame(action: action) {
            VStack(alignment: .leading, spacing: 0) {
                Image(systemName: "music.note.list")
                    .font(.system(size: 20, weight: .semibold))
                    .foregroundColor(tint)
                    .frame(width: 48, height: 48)
                    .background(tint.opacity(0.15))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                Spacer(minLength: 8)
                Text(playlist.name)
                    .font(.app(15, .bold))
                    .foregroundColor(.textPrimary)
                    .lineLimit(1)
                Text("\(playlist.postCount) Bulmaca")
                    .font(.app(13))
                    .foregroundColor(.textMuted)
                    .padding(.top, 4)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(20)
        }
    }
}
