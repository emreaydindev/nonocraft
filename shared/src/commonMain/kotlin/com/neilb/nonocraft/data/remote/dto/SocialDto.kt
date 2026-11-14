package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: String,
    val puzzleId: String,
    val userId: String,
    val username: String = "",
    @SerialName("content") val text: String,
    @SerialName("likesCount") val likeCount: Int? = null,
    val isLiked: Boolean? = null,
    val createdAt: String? = null
)

@Serializable
data class CreateCommentRequest(
    @SerialName("content") val text: String
)

@Serializable
data class CommentListResponse(
    val comments: List<CommentDto>
)

@Serializable
data class ReportRequest(
    val puzzleId: String? = null,
    val commentId: String? = null,
    val reason: String
)
