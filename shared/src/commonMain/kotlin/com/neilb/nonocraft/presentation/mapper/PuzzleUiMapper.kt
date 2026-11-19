package com.neilb.nonocraft.presentation.mapper

import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.presentation.model.Difficulty
import com.neilb.nonocraft.presentation.model.PostItem
import kotlin.time.Instant

private const val FALLBACK_ACCENT = 0xFF888888

fun Puzzle.toPostItem(): PostItem {
    return PostItem(
        id = id,
        title = title,
        author = username?.takeIf { it.isNotBlank() },
        createdAtEpochMillis = createdAt?.toEpochMillisOrNull(),
        likes = likeCount,
        comments = commentCount,
        isLiked = isLiked,
        isFollowed = false, // Not directly in Puzzle model, usually handled via separate state
        difficulty = Difficulty.fromApi(difficulty),
        size = size,
        accentColor = paletteHex.firstOrNull()?.hexToArgb() ?: FALLBACK_ACCENT,
        pattern = grid
    )
}

/**
 * The API sends ISO-8601 (`2026-11-17T10:00:00.000Z`) or SQLite's `2026-11-17 10:00:00` (UTC).
 * Null when it is neither.
 */
internal fun String.toEpochMillisOrNull(): Long? {
    val iso = if ('T' in this) this else replace(' ', 'T')
    val withZone = if (iso.endsWith('Z') || iso.contains('+') || iso.lastIndexOf('-') > 9) iso else iso + "Z"
    return runCatching { Instant.parse(withZone).toEpochMilliseconds() }.getOrNull()
}

/** "#RRGGBB" to opaque ARGB; null when the string is not a 6-digit hex colour. */
private fun String.hexToArgb(): Long? {
    val digits = removePrefix("#")
    if (digits.length != 6) return null
    return digits.toLongOrNull(16)?.let { 0xFF000000 or it }
}
