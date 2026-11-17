import Shared
import SwiftUI

enum PostCardAction {
    case like, comment, play, more, profile, follow
}

struct PostCard: View {
    let post: PostItem
    var showFollowButton = true
    let onAction: (PostCardAction) -> Void

    private var accent: Color { Color(argb: post.accentColor) }

    var body: some View {
        DashboardCard(cornerRadius: 20) {
            VStack(spacing: 16) {
                header
                preview
                stats
                footer
            }
            .padding(16)
        }
    }

    private var header: some View {
        HStack {
            Button { onAction(.profile) } label: {
                HStack(spacing: 12) {
                    Text(post.author.prefix(1).uppercased())
                        .font(.app(14, .bold))
                        .foregroundColor(accent)
                        .frame(width: 36, height: 36)
                        .background(Color.surfaceVariantDark)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(accent.opacity(0.5), lineWidth: 1))
                    VStack(alignment: .leading, spacing: 2) {
                        Text("@\(post.author)")
                            .font(.app(14, .bold))
                            .foregroundColor(.textPrimary)
                        Text(post.timeAgo)
                            .font(.caption)
                            .foregroundColor(.textMuted)
                    }
                }
            }
            .buttonStyle(.plain)

            Spacer()

            if showFollowButton {
                Button(post.isFollowed ? "Takipte" : "Takip Et") { onAction(.follow) }
                    .font(.footnote.weight(.bold))
                    .foregroundStyle(post.isFollowed ? Color.textPrimary : Color.onNeon)
                    .buttonStyle(post.isFollowed ? AppGlassButtonStyle.appGlass : .appGlassProminent)
                    .tint(.neonCyan)
                    .controlSize(.small)
            }

            Button { onAction(.more) } label: {
                Image(systemName: "ellipsis")
                    .font(.system(size: 18, weight: .semibold))
                    .foregroundColor(.textMuted)
                    .frame(width: 32, height: 32)
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Diğer seçenekler")
        }
    }

    private var preview: some View {
        let rows = post.pattern.map { $0.map(\.intValue) }
        return VStack(spacing: 3) {
            ForEach(rows.indices, id: \.self) { row in
                HStack(spacing: 3) {
                    ForEach(rows[row].indices, id: \.self) { col in
                        RoundedRectangle(cornerRadius: 2)
                            .fill(rows[row][col] == 1 ? accent : Color.surfaceVariantDark)
                            .aspectRatio(1, contentMode: .fit)
                    }
                }
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity)
        .background(Color.backgroundDark)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.borderDark, lineWidth: 1))
        .accessibilityHidden(true)
    }

    private var stats: some View {
        HStack(spacing: 20) {
            Button { onAction(.like) } label: {
                HStack(spacing: 6) {
                    Image(systemName: post.isLiked ? "heart.fill" : "heart")
                        .font(.system(size: 20))
                        .foregroundColor(post.isLiked ? .dangerRed : .textPrimary)
                    Text("\(post.likes)")
                        .font(.app(14, .medium))
                        .foregroundColor(.textPrimary)
                }
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Beğen, \(post.likes) beğeni")

            Button { onAction(.comment) } label: {
                HStack(spacing: 6) {
                    Image(systemName: "bubble.left")
                        .font(.system(size: 18))
                    Text("\(post.comments)")
                        .font(.app(14, .medium))
                }
                .foregroundColor(.textPrimary)
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Yorumlar, \(post.comments) yorum")

            Spacer()
        }
    }

    private var footer: some View {
        HStack {
            VStack(alignment: .leading, spacing: 2) {
                Text(post.title)
                    .font(.app(15, .bold))
                    .foregroundColor(.textPrimary)
                    .lineLimit(1)
                Text("\(post.size) · \(post.difficulty)")
                    .font(.footnote)
                    .foregroundColor(.textMuted)
            }
            Spacer()
            Button { onAction(.play) } label: {
                Label("Oyna", systemImage: "play.fill")
                    .font(.subheadline.weight(.bold))
                    .foregroundStyle(Color.onNeon)
            }
            .buttonStyle(.appGlassProminent)
            .tint(.neonCyan)
        }
        .padding(12)
        .background(Color.surfaceVariantDark)
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}
