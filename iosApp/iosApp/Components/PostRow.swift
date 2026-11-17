import Shared
import SwiftUI

/// Compact post row for the list pane of a two-pane layout: pattern thumbnail, title, author and likes.
/// The full `PostCard` is shown next to it as the preview.
struct PostRow: View {
    let post: PostItem
    let isSelected: Bool
    let action: () -> Void

    private var accent: Color { Color(argb: post.accentColor) }

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                PostPatternThumbnail(pattern: post.pattern.map { $0.map(\.intValue) }, accent: accent)
                    .frame(width: 56, height: 56)
                VStack(alignment: .leading, spacing: 2) {
                    Text(post.title)
                        .font(.app(15, .bold))
                        .foregroundStyle(Color.textPrimary)
                        .lineLimit(2)
                    Text("@\(post.author)")
                        .font(.footnote)
                        .foregroundStyle(Color.textSecondary)
                        .lineLimit(1)
                    Label("\(post.likes)", systemImage: post.isLiked ? "heart.fill" : "heart")
                        .font(.caption)
                        .foregroundStyle(Color.textMuted)
                }
                Spacer(minLength: 0)
            }
            .padding(12)
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
        .accessibilityElement(children: .combine)
        .accessibilityAddTraits(isSelected ? [.isButton, .isSelected] : .isButton)
    }
}

/// The post's pixel pattern drawn small, for thumbnails.
struct PostPatternThumbnail: View {
    let pattern: [[Int]]
    let accent: Color

    var body: some View {
        Canvas { context, size in
            let rows = pattern.count
            let cols = pattern.map(\.count).max() ?? 0
            guard rows > 0, cols > 0 else { return }
            let cell = min(size.width / CGFloat(cols), size.height / CGFloat(rows))
            let origin = CGPoint(x: (size.width - cell * CGFloat(cols)) / 2, y: (size.height - cell * CGFloat(rows)) / 2)
            for (row, values) in pattern.enumerated() {
                for (col, value) in values.enumerated() {
                    let rect = CGRect(
                        x: origin.x + CGFloat(col) * cell, y: origin.y + CGFloat(row) * cell, width: cell, height: cell
                    ).insetBy(dx: cell * 0.06, dy: cell * 0.06)
                    context.fill(Path(rect), with: .color(value == 1 ? accent : .surfaceVariantDark))
                }
            }
        }
        .padding(6)
        .background(Color.backgroundDark)
        .clipShape(RoundedRectangle(cornerRadius: 10))
        .accessibilityHidden(true)
    }
}
