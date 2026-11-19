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

    @ScaledMetric(relativeTo: .subheadline) private var avatarSize: CGFloat = 36

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
        AdaptiveStack(spacing: 8) {
            Button { onAction(.profile) } label: {
                HStack(spacing: 12) {
                    Text(DisplayText.author(post.author).prefix(1).uppercased(with: .current))
                        .font(.app(14, .bold))
                        .foregroundColor(accent)
                        .frame(width: avatarSize, height: avatarSize)
                        .background(Color.surfaceVariantDark)
                        .clipShape(Circle())
                        .overlay(Circle().stroke(accent.opacity(0.5), lineWidth: 1))
                        .accessibilityHidden(true) // the initial repeats the name read next
                    VStack(alignment: .leading, spacing: 2) {
                        Text(DisplayText.handle(post.author))
                            .font(.app(14, .bold))
                            .foregroundColor(.textPrimary)
                        Text(DisplayText.relativeTime(post.createdAtEpochMillis))
                            .font(.caption)
                            .foregroundColor(.textMuted)
                    }
                }
            }
            .buttonStyle(.plain)

            Spacer(minLength: 0)

            HStack(spacing: 8) {
                if showFollowButton {
                    Button(L10n.string(post.isFollowed ? "post_following" : "post_follow")) { onAction(.follow) }
                        .font(.footnote.weight(.bold))
                        .foregroundStyle(post.isFollowed ? Color.textPrimary : Color.onNeon)
                        .buttonStyle(post.isFollowed ? AppGlassButtonStyle.appGlass : .appGlassProminent)
                        .tint(.neonCyan)
                        .controlSize(.small)
                }

                Button { onAction(.more) } label: {
                    Image(systemName: "ellipsis")
                        .font(.title3.weight(.semibold))
                        .foregroundColor(.textMuted)
                        .frame(minWidth: 44, minHeight: 44)
                        .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                .accessibilityLabel(L10n.string("post_more_options"))
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
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
                        .font(.title3)
                        .foregroundColor(post.isLiked ? .dangerRed : .textPrimary)
                    Text("\(post.likes)")
                        .font(.app(14, .medium))
                        .foregroundColor(.textPrimary)
                }
                .frame(minHeight: 44)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel(L10n.format("post_like_a11y", post.likes))
            .accessibilityValue(post.isLiked ? L10n.string("post_liked") : "")

            Button { onAction(.comment) } label: {
                HStack(spacing: 6) {
                    Image(systemName: "bubble.left")
                        .font(.title3)
                    Text("\(post.comments)")
                        .font(.app(14, .medium))
                }
                .foregroundColor(.textPrimary)
                .frame(minHeight: 44)
                .contentShape(Rectangle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel(L10n.format("post_comments_a11y", post.comments))

            Spacer()
        }
    }

    private var footer: some View {
        AdaptiveStack(spacing: 10) {
            VStack(alignment: .leading, spacing: 2) {
                Text(post.title)
                    .font(.app(15, .bold))
                    .foregroundColor(.textPrimary)
                    .lineLimit(1)
                Text([DisplayText.boardSize(post.size), post.difficulty?.label].compactMap { $0 }.joined(separator: " · "))
                    .font(.footnote)
                    .foregroundColor(.textMuted)
            }
            Spacer(minLength: 0)
            Button { onAction(.play) } label: {
                Label(L10n.string("common_play"), systemImage: "play.fill")
                    .font(.subheadline.weight(.bold))
                    .foregroundStyle(Color.onNeon)
            }
            .buttonStyle(.appGlassProminent)
            .tint(.neonCyan)
            .accessibilityIdentifier("post_play")
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(Color.surfaceVariantDark)
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}
