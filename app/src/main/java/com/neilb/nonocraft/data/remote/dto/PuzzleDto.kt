package com.neilb.nonocraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PuzzleDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("userId") val userId: String,
    @SerializedName("username") val username: String?,
    @SerializedName("size") val size: Int,
    @SerializedName("paletteHex") val paletteHex: List<String>,
    @SerializedName("grid") val grid: List<List<Int>>,
    @SerializedName("type") val type: String?,
    @SerializedName("difficulty") val difficulty: String?,
    @SerializedName("isPublic") val isPublic: Boolean?,
    @SerializedName("isSolvable") val isSolvable: Boolean?,
    @SerializedName("likeCount") val likeCount: Int?,
    @SerializedName("commentCount") val commentCount: Int?,
    @SerializedName("playCount") val playCount: Int?,
    @SerializedName("isLiked") val isLiked: Boolean?,
    @SerializedName("isCompleted") val isCompleted: Boolean?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("dailyDate") val dailyDate: String?
)

data class CreatePuzzleRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("size") val size: Int,
    @SerializedName("paletteHex") val paletteHex: List<String>,
    @SerializedName("grid") val grid: List<List<Int>>,
    @SerializedName("isPublic") val isPublic: Boolean = true,
    @SerializedName("difficulty") val difficulty: String? = null
)

data class PuzzleListResponse(
    @SerializedName("puzzles") val puzzles: List<PuzzleDto>
)

data class LikeResponse(
    @SerializedName("liked") val liked: Boolean?,
    @SerializedName("likeCount") val likeCount: Int?
)

data class CompleteResponse(
    @SerializedName("starsAwarded") val starsAwarded: Int,
    @SerializedName("totalStars") val totalStars: Int
)
