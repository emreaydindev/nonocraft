package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class FeedItemDto(
    val id: String,
    val title: String,
    val description: String?,
    val userId: String,
    val username: String?,
    val size: Int,
    val paletteHex: List<String>,
    val difficulty: String?,
    val likeCount: Int?,
    val commentCount: Int?,
    val isLiked: Boolean?,
    val isCompleted: Boolean?,
    val createdAt: String?
)

@Serializable
data class FeedListResponse(
    val posts: List<FeedItemDto>
)
