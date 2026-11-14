package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedItemDto(
    val id: String,
    val title: String,
    val description: String? = null,
    @SerialName("creatorId") val userId: String = "",
    @SerialName("creatorUsername") val username: String? = null,
    val size: Int,
    @Serializable(with = PaletteHexSerializer::class) val paletteHex: List<String> = emptyList(),
    val difficulty: String? = null,
    @SerialName("likesCount") val likeCount: Int? = null,
    @SerialName("commentsCount") val commentCount: Int? = null,
    val isLiked: Boolean? = null,
    val isCompleted: Boolean? = null,
    val createdAt: String? = null
)

@Serializable
data class FeedListResponse(
    val posts: List<FeedItemDto>
)
