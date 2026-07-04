package com.neilb.nonocraft.presentation.mapper

import androidx.compose.ui.graphics.Color
import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.presentation.model.PostItem

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
        accentColor = if (paletteHex.isNotEmpty()) Color(android.graphics.Color.parseColor(paletteHex.first())) else Color.Gray,
        pattern = grid
    )
}
