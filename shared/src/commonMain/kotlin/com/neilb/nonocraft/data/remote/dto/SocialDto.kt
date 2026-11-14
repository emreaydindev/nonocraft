package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class CommentDto(
    val id: String,
    val puzzleId: String,
    val userId: String,
    val username: String,
    val text: String,
    val likeCount: Int?,
    val isLiked: Boolean?,
    val createdAt: String?
)

@Serializable
data class CreateCommentRequest(
    val text: String
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
