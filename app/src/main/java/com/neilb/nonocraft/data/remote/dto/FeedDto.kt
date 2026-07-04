package com.neilb.nonocraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FeedItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("userId") val userId: String,
    @SerializedName("username") val username: String?,
    @SerializedName("size") val size: Int,
    @SerializedName("paletteHex") val paletteHex: List<String>,
    @SerializedName("difficulty") val difficulty: String?,
    @SerializedName("likeCount") val likeCount: Int?,
    @SerializedName("commentCount") val commentCount: Int?,
    @SerializedName("isLiked") val isLiked: Boolean?,
    @SerializedName("isCompleted") val isCompleted: Boolean?,
    @SerializedName("createdAt") val createdAt: String?
)

data class FeedListResponse(
    @SerializedName("posts") val posts: List<FeedItemDto>
)
