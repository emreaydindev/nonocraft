import Shared
import SwiftUI

struct LibraryScreen: View {
    let onNavigateToCrafting: () -> Void
    let onNavigateToPremium: () -> Void
    let onNavigateToCategory: (String) -> Void
    let onNavigateToPlaylist: (String) -> Void

    let onNavigateToGame: (String) -> Void
    let onNavigateToProfile: (String) -> Void

    @StateObject private var model = ObservedViewModel({ $0.libraryViewModel() }, state: \.uiState)
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    @State private var showCreatePlaylist = false
    @State private var newPlaylistName = ""
    /// Section or playlist shown in the content pane of the two-pane layout.
    @State private var selectedId = "liked"
    /// Mirrors the size class so the event loop below reads the current layout.
    @State private var isTwoPane = false

    private var uiState: LibraryUiState { model.state }
    /// Two columns on phones, more on wider single-column windows.
    private let columns = [GridItem(.adaptive(minimum: 150, maximum: 260), spacing: 16)]

    private struct Section: Identifiable {
        let id: String
        let title: String
        let subtitle: String
        let icon: String
        let tint: Color
        var isPremium = false
        let action: () -> Void
    }

    private var sections: [Section] {
        [
            Section(
                id: "liked", title: "Beğenilenler", subtitle: "\(uiState.likedArtsCount) Pixelart",
                icon: "heart.fill", tint: .dangerRed
            ) { send(LibraryActionOnCategoryClick(categoryId: "liked")) },
            Section(
                id: "downloaded", title: "İndirilenler", subtitle: "\(uiState.downloadedLevelsCount) Bölüm",
                icon: "arrow.down.to.line", tint: .gameGold, isPremium: true
            ) {
                send(LibraryActionOnCategoryClick(categoryId: "downloaded"))
                onNavigateToPremium()
            },
            Section(
                id: "shared", title: "Paylaşılanlar", subtitle: "\(uiState.sharedArtsCount) Tasarım",
                icon: "paintpalette.fill", tint: .neonCyan
            ) { send(LibraryActionOnCategoryClick(categoryId: "shared")) },
            Section(
                id: "drafts", title: "Taslaklar", subtitle: "\(uiState.draftArtsCount) Kayıt",
                icon: "pencil", tint: .textMuted
            ) {
                send(LibraryActionOnCategoryClick(categoryId: "drafts"))
                onNavigateToCrafting()
            },
            Section(
                id: "completed", title: "Çözülenler", subtitle: "\(uiState.completedLevelsCount) Bölüm",
                icon: "checkmark.circle.fill", tint: Color(hex: 0x4CAF50)
            ) { send(LibraryActionOnCategoryClick(categoryId: "completed")) },
            Section(
                id: "dailies", title: "Günlük Serüven", subtitle: "\(uiState.completedDailiesCount) Çözüm",
                icon: "calendar", tint: Color(hex: 0xB388FF)
            ) { send(LibraryActionOnCategoryClick(categoryId: "dailies")) },
        ]
    }

    var body: some View {
        Group {
            if horizontalSizeClass == .regular {
                twoPane
            } else {
                singleColumn
            }
        }
        .background { ScreenBackground { EmptyView() } }
        .navigationTitle("Kütüphanem")
        // Large title on the same row as the toolbar button instead of a row below it.
        .toolbarTitleDisplayMode(.inlineLarge)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button("Tasarım Stüdyosu", systemImage: "paintbrush.pointed.fill", action: onNavigateToCrafting)
            }
        }
        .onChange(of: horizontalSizeClass, initial: true) { _, sizeClass in
            isTwoPane = sizeClass == .regular
        }
        .task {
            for await event in model.viewModel.uiEvent {
                switch onEnum(of: event) {
                case .navigateToCategory(let event):
                    if isTwoPane { selectedId = event.categoryId } else { onNavigateToCategory(event.categoryId) }
                case .navigateToPlaylist(let event):
                    if isTwoPane { selectedId = event.playlistId } else { onNavigateToPlaylist(event.playlistId) }
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

    /// Phones and narrow windows: section cards and playlists in grids; each opens its own page.
    private var singleColumn: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                LazyVGrid(columns: columns, spacing: 16) {
                    ForEach(sections) { section in
                        LibraryGridCard(
                            title: section.title, subtitle: section.subtitle, icon: section.icon,
                            tint: section.tint, isPremium: section.isPremium, action: section.action
                        )
                    }
                }

                playlistsHeader
                    .padding(.top, 24)

                LazyVGrid(columns: columns, spacing: 16) {
                    ForEach(uiState.playlists, id: \.id) { playlist in
                        PlaylistGridCard(playlist: playlist) {
                            send(LibraryActionOnPlaylistClick(playlistId: playlist.id))
                        }
                    }
                }
            }
            .padding(.horizontal, 18)
            .padding(.top, 8)
            .padding(.bottom, 24)
        }
    }

    /// iPad, the iPhone Duo inner screen and wide windows: sections and playlists on the left, the picked
    /// one's posts on the right.
    private var twoPane: some View {
        HStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 8) {
                    ForEach(sections) { section in
                        LibrarySectionRow(
                            title: section.title, subtitle: section.subtitle, isSelected: selectedId == section.id,
                            isPremium: section.isPremium, action: section.action
                        ) {
                            LibrarySectionIcon(systemName: section.icon, tint: section.tint, shape: Circle())
                        }
                    }

                    playlistsHeader
                        .padding(.top, 16)

                    ForEach(uiState.playlists, id: \.id) { playlist in
                        let tint = Color(argb: playlist.coverColor)
                        LibrarySectionRow(
                            title: playlist.name, subtitle: "\(playlist.postCount) Bulmaca",
                            isSelected: selectedId == playlist.id
                        ) {
                            send(LibraryActionOnPlaylistClick(playlistId: playlist.id))
                        } icon: {
                            LibrarySectionIcon(systemName: "music.note.list", tint: tint, shape: RoundedRectangle(cornerRadius: 12))
                        }
                    }
                }
                .padding(.horizontal, 18)
                .padding(.top, 8)
                .padding(.bottom, 24)
            }
            .frame(width: 340)

            Rectangle()
                .fill(Color.borderDark)
                .frame(width: 0.5)
                .ignoresSafeArea(edges: .bottom)

            LibraryDetailContent(
                categoryId: selectedId,
                showsTitleHeader: true,
                onNavigateToGame: onNavigateToGame,
                onNavigateToProfile: onNavigateToProfile
            )
            // A fresh view model per section.
            .id(selectedId)
            .frame(maxWidth: .infinity)
        }
    }

    private var playlistsHeader: some View {
        HStack {
            Text("Oynatma Listeleri")
                .font(.app(22, .bold))
                .foregroundStyle(Color.textPrimary)
                .accessibilityAddTraits(.isHeader)
            Spacer()
            Button("Yeni Liste", systemImage: "plus") { send(LibraryActionOnCreatePlaylist.shared) }
                .labelStyle(.iconOnly)
                .font(.title3.weight(.semibold))
                .buttonStyle(.appGlass)
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
                        ProBadge()
                    }
                }
                Spacer(minLength: 8)
                Text(title)
                    .font(.app(16, .bold))
                    .foregroundColor(.textPrimary)
                    .multilineTextAlignment(.leading)
                Text(subtitle)
                    .font(.subheadline)
                    .foregroundColor(.textMuted)
                    .padding(.top, 4)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(20)
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
                    .font(.subheadline)
                    .foregroundColor(.textMuted)
                    .padding(.top, 4)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(20)
        }
    }
}

private struct LibrarySectionIcon<S: Shape>: View {
    let systemName: String
    let tint: Color
    let shape: S

    var body: some View {
        Image(systemName: systemName)
            .font(.system(size: 18, weight: .semibold))
            .foregroundColor(tint)
            .frame(width: 44, height: 44)
            .background(tint.opacity(0.15))
            .clipShape(shape)
    }
}

/// A section or playlist in the list pane of the two-pane Library.
private struct LibrarySectionRow<Icon: View>: View {
    let title: String
    let subtitle: String
    let isSelected: Bool
    var isPremium = false
    let action: () -> Void
    @ViewBuilder var icon: Icon

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                icon
                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(.app(15, .bold))
                        .foregroundColor(.textPrimary)
                        .multilineTextAlignment(.leading)
                    Text(subtitle)
                        .font(.footnote)
                        .foregroundColor(.textMuted)
                }
                Spacer(minLength: 0)
                if isPremium {
                    ProBadge()
                }
            }
            .padding(10)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(isSelected ? Color.surfaceVariantDark : Color.surfaceDark)
            .clipShape(RoundedRectangle(cornerRadius: 16))
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .stroke(isSelected ? Color.neonCyan : Color.borderDark, lineWidth: isSelected ? 1.5 : 0.5)
            )
            .contentShape(RoundedRectangle(cornerRadius: 16))
        }
        .buttonStyle(.plain)
        .accessibilityAddTraits(isSelected ? .isSelected : [])
    }
}
