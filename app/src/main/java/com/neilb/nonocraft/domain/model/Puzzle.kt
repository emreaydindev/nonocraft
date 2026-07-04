package com.neilb.nonocraft.domain.model

data class Clue(
    val count: Int,
    val colorIndex: Int
)

data class Puzzle(
    val id: String,
    val title: String,
    val description: String?,
    val userId: String,
    val username: String?,
    val size: Int,
    val paletteHex: List<String>,
    val grid: List<List<Int>>,
    val type: String?,
    val difficulty: String?,
    val isPublic: Boolean,
    val likeCount: Int,
    val commentCount: Int,
    val playCount: Int,
    val isLiked: Boolean,
    val isCompleted: Boolean,
    val createdAt: String?,
    val dailyDate: String?,
    val timeLimitSeconds: Long? = null,
    val maxLives: Int = 3
)

data class CompletionResult(
    val starsAwarded: Int,
    val totalStars: Int
)

data class LikeResult(
    val isLiked: Boolean,
    val likeCount: Int
)

data class Comment(
    val id: String,
    val puzzleId: String,
    val userId: String,
    val username: String,
    val text: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val createdAt: String?
)
