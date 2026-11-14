package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class PuzzleDto(
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
    val isPublic: Boolean?,
    val isSolvable: Boolean?,
    val likeCount: Int?,
    val commentCount: Int?,
    val playCount: Int?,
    val isLiked: Boolean?,
    val isCompleted: Boolean?,
    val createdAt: String?,
    val dailyDate: String?
)

@Serializable
data class CreatePuzzleRequest(
    val title: String,
    val description: String? = null,
    val size: Int,
    val paletteHex: List<String>,
    val grid: List<List<Int>>,
    val isPublic: Boolean = true,
    val difficulty: String? = null
)

@Serializable
data class PuzzleListResponse(
    val puzzles: List<PuzzleDto>
)

@Serializable
data class LikeResponse(
    val liked: Boolean?,
    val likeCount: Int?
)

@Serializable
data class CompleteResponse(
    val starsAwarded: Int,
    val totalStars: Int
)
