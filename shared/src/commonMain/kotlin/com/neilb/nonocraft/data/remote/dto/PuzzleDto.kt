@file:OptIn(ExperimentalSerializationApi::class)

package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class PuzzleDto(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerialName("creatorId") val userId: String = "",
    @SerialName("creatorUsername") val username: String? = null,
    val size: Int,
    @Serializable(with = PaletteHexSerializer::class) val paletteHex: List<String> = emptyList(),
    @Serializable(with = GridSerializer::class) val grid: List<List<Int>> = emptyList(),
    val type: String? = null,
    val difficulty: String? = null,
    val isPublic: Boolean? = null,
    val isSolvable: Boolean? = null,
    @SerialName("likesCount") val likeCount: Int? = null,
    @SerialName("commentsCount") val commentCount: Int? = null,
    @SerialName("playsCount") val playCount: Int? = null,
    val isLiked: Boolean? = null,
    val isCompleted: Boolean? = null,
    val createdAt: String? = null,
    @JsonNames("daily_date") val dailyDate: String? = null,
    val timeLimitSeconds: Long? = null,
    val maxLives: Int? = null
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
    val liked: Boolean? = null,
    @SerialName("likesCount") val likeCount: Int? = null
)

@Serializable
data class CompleteResponse(
    val starsAwarded: Int = 0,
    val totalStars: Int = 0,
    val crystalsEarned: Int = 0
)
