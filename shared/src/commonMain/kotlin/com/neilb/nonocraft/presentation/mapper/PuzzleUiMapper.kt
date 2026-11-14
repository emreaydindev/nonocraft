package com.neilb.nonocraft.presentation.mapper

import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.presentation.model.PostItem

private const val FALLBACK_ACCENT = 0xFF888888

fun Puzzle.toPostItem(): PostItem {
    return PostItem(
        id = id,
        title = title,
        author = username ?: "Anonim",
        timeAgo = createdAt ?: "", // Ideally map to "2h ago" etc
        likes = likeCount,
        comments = commentCount,
        isLiked = isLiked,
        isFollowed = false, // Not directly in Puzzle model, usually handled via separate state
        difficulty = difficulty ?: "Orta",
        size = "${size}×${size}",
        accentColor = paletteHex.firstOrNull()?.hexToArgb() ?: FALLBACK_ACCENT,
        pattern = grid
    )
}

/** "#RRGGBB" to opaque ARGB; null when the string is not a 6-digit hex colour. */
private fun String.hexToArgb(): Long? {
    val digits = removePrefix("#")
    if (digits.length != 6) return null
    return digits.toLongOrNull(16)?.let { 0xFF000000 or it }
}
